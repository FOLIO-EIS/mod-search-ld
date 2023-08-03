package org.folio.search.controller;

import static org.folio.search.model.service.CqlResourceIdsRequest.HOLDINGS_ID_PATH;
import static org.folio.search.model.service.CqlResourceIdsRequest.INSTANCE_ID_PATH;
import static org.folio.search.utils.SearchUtils.INSTANCE_RESOURCE;
import static org.folio.search.utils.TestConstants.TENANT_ID;
import static org.folio.search.utils.TestUtils.OBJECT_MAPPER;
import static org.folio.search.utils.TestUtils.randomId;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.folio.search.domain.dto.ResourceId;
import org.folio.search.domain.dto.ResourceIds;
import org.folio.search.model.service.CqlResourceIdsRequest;
import org.folio.search.service.ResourceIdsStreamHelper;
import org.folio.search.service.consortia.ResourceIdServiceDecorator;
import org.folio.search.service.consortia.ResourceIdsJobServiceDecorator;
import org.folio.search.support.base.TenantConfig;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.spring.test.type.UnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@UnitTest
@WebMvcTest(ResourcesIdsController.class)
@Import({ApiExceptionHandler.class, ResourceIdsStreamHelper.class, ResourceIdsJobServiceDecorator.class,
  ResourceIdServiceDecorator.class, TenantConfig.class})
class ResourcesIdsControllerTest {

  @Autowired
  private String centralTenant;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ResourceIdServiceDecorator resourceIdService;
  @MockBean
  private ResourceIdsJobServiceDecorator resourceIdsJobService;

  @Test
  void getHoldingsIds_positive() throws Exception {
    var cqlQuery = "id=*";
    var holdingId = randomId();
    var request = CqlResourceIdsRequest.of(INSTANCE_RESOURCE, centralTenant, cqlQuery, HOLDINGS_ID_PATH);

    doAnswer(inv -> {
      var out = (OutputStream) inv.getArgument(1);
      var resourceIds = new ResourceIds().totalRecords(1).ids(List.of(new ResourceId().id(holdingId)));
      out.write(OBJECT_MAPPER.writeValueAsBytes(resourceIds));
      return null;
    }).when(resourceIdService).streamResourceIdsAsJson(eq(request), any(OutputStream.class));

    var requestBuilder = get("/search/holdings/ids")
      .queryParam("query", cqlQuery)
      .contentType(APPLICATION_JSON)
      .header(XOkapiHeaders.TENANT, TENANT_ID);

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON))
      .andExpect(jsonPath("$.totalRecords", is(1)))
      .andExpect(jsonPath("$.ids[*].id", is(List.of(holdingId))));
  }

  @Test
  void getHoldingsIdsTextType_positive() throws Exception {
    var cqlQuery = "id=*";
    var holdingId = randomId();
    var request = CqlResourceIdsRequest.of(INSTANCE_RESOURCE, centralTenant, cqlQuery, HOLDINGS_ID_PATH);

    doAnswer(inv -> {
      var out = (OutputStream) inv.getArgument(1);
      out.write(OBJECT_MAPPER.writeValueAsBytes(holdingId));
      return null;
    }).when(resourceIdService).streamResourceIdsAsText(eq(request), any(OutputStream.class));

    var requestBuilder = get("/search/holdings/ids")
      .queryParam("query", cqlQuery)
      .contentType(TEXT_PLAIN)
      .header(XOkapiHeaders.TENANT, TENANT_ID);

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andExpect(content().contentType(TEXT_PLAIN))
      .andExpect(content().string(containsString(holdingId)));
  }

  @Test
  void getInstanceIds_positive() throws Exception {
    var cqlQuery = "id=*";
    var instanceId = randomId();
    var request = CqlResourceIdsRequest.of(INSTANCE_RESOURCE, centralTenant, cqlQuery, INSTANCE_ID_PATH);

    doAnswer(inv -> {
      var out = (OutputStream) inv.getArgument(1);
      var resourceIds = new ResourceIds().totalRecords(1).ids(List.of(new ResourceId().id(instanceId)));
      out.write(OBJECT_MAPPER.writeValueAsBytes(resourceIds));
      return null;
    }).when(resourceIdService).streamResourceIdsAsJson(eq(request), any(OutputStream.class));

    var requestBuilder = get("/search/instances/ids")
      .queryParam("query", cqlQuery)
      .contentType(APPLICATION_JSON)
      .header(XOkapiHeaders.TENANT, TENANT_ID);

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.totalRecords", is(1)))
      .andExpect(jsonPath("$.ids[0].id", is(instanceId)));
  }

  @Test
  void getInstanceIdsTextType_positive() throws Exception {
    var cqlQuery = "id=*";
    var instanceId = randomId();
    var request = CqlResourceIdsRequest.of(INSTANCE_RESOURCE, centralTenant, cqlQuery, INSTANCE_ID_PATH);

    doAnswer(inv -> {
      var out = (OutputStream) inv.getArgument(1);
      out.write(instanceId.getBytes(StandardCharsets.UTF_8));
      return null;
    }).when(resourceIdService).streamResourceIdsAsText(eq(request), any(OutputStream.class));

    var requestBuilder = get("/search/instances/ids")
      .queryParam("query", cqlQuery)
      .contentType(TEXT_PLAIN)
      .header(XOkapiHeaders.TENANT, TENANT_ID);

    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk())
      .andExpect(content().contentType(TEXT_PLAIN_VALUE))
      .andExpect(content().string(instanceId));
  }
}