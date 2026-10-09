package dev.berren.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

	private final ApplicationModules modules = ApplicationModules.of(PortfolioApplication.class);

	@Test
	void verifiesModularStructure() {
		modules.verify();
	}

}
