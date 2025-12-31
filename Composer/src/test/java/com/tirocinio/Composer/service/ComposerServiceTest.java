package com.tirocinio.Composer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComposerServiceTest {

    private ComposerService composerService;

    @BeforeEach
    void setUp() {
        composerService = new ComposerService();
    }

    @Test
    @DisplayName("Composizione Completa - Tutti i frammenti presenti e validi")
    void testComposeConfiguration_Success() {
        Map<String, String> fragments = new HashMap<>();

        String weatherFragment = "<Weather>Clear</Weather>";

        String vehicleFragment =
                "<ScenarioObject name=\"EgoVehicle\">\n" +
                        "  <Vehicle name=\"vehicle.tesla.model3\"/>\n" +
                        "</ScenarioObject>\n" +
                        "<Private entityRef=\"EgoVehicle\">\n" +
                        "  <ControllerAction>...</ControllerAction>\n" +
                        "</Private>";

        String scenarioFragment =
                "<RoadNetwork>\n" +
                        "  <LogicFile filepath=\"Town01\"/>\n" +
                        "</RoadNetwork>\n" +
                        "<ScenarioObject name=\"Traffic0\">\n" +
                        "  <Vehicle name=\"vehicle.audi.etron\"/>\n" +
                        "</ScenarioObject>\n" +
                        "<Private entityRef=\"Traffic0\">...</Private>";

        fragments.put("weatherFragment", weatherFragment);
        fragments.put("vehicleFragment", vehicleFragment);
        fragments.put("scenarioFragment", scenarioFragment);

        String resultXml = composerService.composeConfiguration(fragments);

        assertNotNull(resultXml);


        assertTrue(resultXml.contains("<OpenScenario>"));
        assertTrue(resultXml.contains("<FileHeader"));


        assertTrue(resultXml.contains("<RoadNetwork>"));
        assertTrue(resultXml.contains("filepath=\"Town01\""));


        assertTrue(resultXml.contains("<Entities>"));
        assertTrue(resultXml.contains("name=\"EgoVehicle\""));
        assertTrue(resultXml.contains("name=\"Traffic0\""));


        assertTrue(resultXml.contains("<Storyboard>"));
        assertTrue(resultXml.contains("<Init>"));
        assertTrue(resultXml.contains("<GlobalAction>"));
        assertTrue(resultXml.contains("<Weather>Clear</Weather>"));
        assertTrue(resultXml.contains("entityRef=\"EgoVehicle\""));
    }

    @Test
    @DisplayName("Gestione Input Parziale - Frammenti vuoti o mancanti")
    void testComposeConfiguration_PartialInput() {
        Map<String, String> fragments = new HashMap<>();
        fragments.put("weatherFragment", "");
        fragments.put("scenarioFragment", "<RoadNetwork><LogicFile/></RoadNetwork>");

        String resultXml = composerService.composeConfiguration(fragments);

        assertNotNull(resultXml);
        assertTrue(resultXml.contains("<OpenScenario>"));
        assertTrue(resultXml.contains("<RoadNetwork>"));
        assertTrue(resultXml.contains("<Entities>"));
    }

    @Test
    @DisplayName("Fix Controller Action - Aggiunta automatica di <Properties/>")
    void testFixControllerActions_Logic() {

        Map<String, String> fragments = new HashMap<>();
        String brokenControllerFragment =
                "<Private entityRef=\"Test\">\n" +
                        "  <Controller>\n" +
                        "    <SomeAction/>\n" +
                        "  </Controller>\n" +
                        "</Private>";

        fragments.put("vehicleFragment", brokenControllerFragment);

        String resultXml = composerService.composeConfiguration(fragments);

        assertTrue(resultXml.contains("<Properties/>"), "Il metodo fixControllerActions avrebbe dovuto iniettare Properties");
    }
}