package br.com.wallmart.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ShorterRoute {

	@NonNull
	private BigDecimal distance;
	@NonNull
	private BigDecimal cost;
	private List<String> routes = new ArrayList<>();

	public void addRoute(String routeName) {
		this.routes.add(routeName);
	}

}
