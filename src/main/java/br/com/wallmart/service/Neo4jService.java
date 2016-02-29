package br.com.wallmart.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.Relationship;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import br.com.wallmart.model.DistanceAndRoute;
import br.com.wallmart.model.Map;
import br.com.wallmart.model.ShorterRoute;
import br.com.wallmart.utils.Config;
import br.com.wallmart.utils.ValidateUtils;

public class Neo4jService {

	private static final String NEO4J_URL = System.getenv("NEO4J_URL") != null ? System.getenv("NEO4J_URL")
			: Config.NEO4J.getString("neo4j.url");

	private RestAPI restAPI;

	public Neo4jService() {
		this.restAPI = new RestAPIFacade(NEO4J_URL.concat("/db/data/"), Config.NEO4J.getString("neo4j.username"),
				Config.NEO4J.getString("neo4j.password"));
	}

	public void persist(Map map) {
		separateNodes(map).stream().forEach(name -> {
			createNode(name);
		});

		map.getRoutes().stream().forEach(route -> {
			createRoute(route.getOrigin(), route.getDestiny(), route.getDistance());
		});
	}

	public ShorterRoute getShorterRoutes(String origin, String destiny, BigDecimal autonomy, BigDecimal fuelPrice) {
		RestNode originNode = findNode(origin);
		ValidateUtils.isNull(originNode, Config.MSG.getString("msg.origin.notFound"));

		RestNode destinyNode = findNode(destiny);
		ValidateUtils.isNull(destinyNode, Config.MSG.getString("msg.destiny.notFound"));

		DistanceAndRoute distanceAndRoute = SearchService.searchShorterRoute(originNode, destinyNode);
		return createShorterRoute(distanceAndRoute, autonomy, fuelPrice);
	}

	private ShorterRoute createShorterRoute(DistanceAndRoute distanceAndRoute, BigDecimal autonomy,
			BigDecimal fuelPrice) {
		BigDecimal distance = distanceAndRoute.getDistance();
		BigDecimal cost = calculateCost(distance, autonomy, fuelPrice);

		ShorterRoute shorterRoute = new ShorterRoute(distance, cost);

		distanceAndRoute.getRouteIds().stream().forEach(id -> {
			RestNode node = restAPI.getNodeById(id);
			shorterRoute.addRoute(node.getProperty("name").toString());
		});

		return shorterRoute;
	}

	private BigDecimal calculateCost(BigDecimal distance, BigDecimal autonomy, BigDecimal fuelPrice) {
		return fuelPrice.multiply(distance).divide(autonomy, 2, RoundingMode.CEILING);
	}

	private void createRoute(String origin, String destiny, BigDecimal distance) {
		RestNode originNode = findOrCreateNode(origin);
		RestNode destinyNode = findOrCreateNode(destiny);

		Relationship relationship = originNode.createRelationshipTo(destinyNode, RouteRelationshipType.ROUTE);
		relationship.setProperty("distance", distance);
	}

	private RestNode findOrCreateNode(String routeName) {
		RestNode node = findNode(routeName);
		if (node == null)
			node = createNode(routeName);

		return node;
	}

	private RestNode findNode(String nodeName) {
		String query = String.format("MATCH (node {name: '%s'}) RETURN node ", nodeName);

		QueryEngine<java.util.Map<String, Object>> engine = new RestCypherQueryEngine(restAPI);
		QueryResult<java.util.Map<String, Object>> queryResult = engine.query(query, Collections.emptyMap());

		Iterator<java.util.Map<String, Object>> iterator = queryResult.iterator();
		if (iterator.hasNext()) {
			java.util.Map<String, Object> row = iterator.next();
			return (RestNode) row.get("node");
		}

		return null;
	}

	private RestNode createNode(String nodeName) {
		java.util.Map<String, Object> node = new HashMap<>();
		node.put("name", nodeName);

		return restAPI.createNode(node);
	}

	private Set<String> separateNodes(Map map) {
		Set<String> nodes = new HashSet<>();

		map.getRoutes().stream().forEach(route -> {
			nodes.add(route.getOrigin());
			nodes.add(route.getDestiny());
		});

		return nodes;
	}
}
