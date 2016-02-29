package br.com.wallmart.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DistanceAndRoute {

	@NonNull
	private BigDecimal distance;
	private List<Long> routeIds = new ArrayList<>();

	public void addRouteId(Long id) {
		this.routeIds.add(id);
	}

}
