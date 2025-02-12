package com.app.controllers.masters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.app.logging.BaseLoggers;
import javax.inject.Inject;
import javax.inject.Named;

import com.app.hibernate.entity.masters.ProductPo;
import com.app.hibernate.service.BaseService;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Inject
    @Named("baseServiceImpl")
    private BaseService baseServiceImpl;

    @GetMapping()
    public ModelAndView toProducts(ModelAndView modelAndView) {
        modelAndView.setViewName("masters/products/products");
        return modelAndView;
    }

    @PostMapping(value = "/populatefilter", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<String> populateProductFilters(@RequestBody String requestJson) {
        BaseLoggers.flowLogger.info("applying filter -> " + requestJson);
        try {
            JSONObject jsonObject = new JSONObject(requestJson);
            String element = jsonObject.getString("element");
            StringBuilder hqlQuery = new StringBuilder(
                    "select distinct p." + element + " from ProductClassificationPo p ");

            if (jsonObject.has("filterJson")) {
                JSONObject filterJson = jsonObject.getJSONObject("filterJson");
                if (filterJson.length() > 0) {
                    hqlQuery.append(" where ");
                }
                Iterator<String> filterKeys = filterJson.keys();
                while (filterKeys.hasNext()) {
                    String key = filterKeys.next();
                    hqlQuery.append(key + "='" + filterJson.get(key).toString().replace("'", "''") + "' ");
                    if (filterKeys.hasNext()) {
                        hqlQuery.append("and ");
                    }
                }
            }
            return (List<String>) baseServiceImpl.getListUsingHql(hqlQuery.toString());
        } catch (JSONException e) {
            BaseLoggers.flowLogger.error("Exception in JsonParsing {}", e);
        }
        return null;
    }

    @PostMapping(value = "/filter", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<ProductPo> getFilteredProducts(@RequestBody String requestJson) {
        BaseLoggers.flowLogger.info("fetching filtered products filter -> " + requestJson);
        try {
            JSONObject filterJson = new JSONObject(requestJson);
            if (filterJson.length() > 0) {
                StringBuilder classificationQuery = new StringBuilder(
                        "select p.id from ProductClassificationPo p where ");
                Iterator<String> filterKeys = filterJson.keys();
                while (filterKeys.hasNext()) {
                    String key = filterKeys.next();
                    classificationQuery
                            .append("p." + key + "='" + filterJson.get(key).toString().replace("'", "''") + "' ");
                    if (filterKeys.hasNext()) {
                        classificationQuery.append("and ");
                    }
                }

                List<Long> productClassificationIds = (List<Long>) baseServiceImpl
                        .getListUsingHql(classificationQuery.toString());

                if (!productClassificationIds.isEmpty()) {
                    Map<String, Object> whereClauseMap = new HashMap<>();
                    whereClauseMap.put("productClassification", productClassificationIds.get(0));
                    return baseServiceImpl.getEntityList(ProductPo.class, whereClauseMap);
                }
            }

        } catch (JSONException e) {
            BaseLoggers.flowLogger.error("Exception in JsonParsing {}", e);
        }
        return null;
    }

    @GetMapping("/get/{id}")
    public ModelAndView getProduct(ModelAndView modelAndView, @PathVariable Long id) {
        BaseLoggers.flowLogger.info("fetching product with id -> " + id);
        modelAndView.setViewName("masters/products/product");
        modelAndView.addObject("product", baseServiceImpl.getEntityByIdWithAllLazyObjects(ProductPo.class, id));
        return modelAndView;
    }

    @GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ProductPo> getAllProduct() {
        BaseLoggers.flowLogger.info("fetching all products");
        return baseServiceImpl.findAll(ProductPo.class);
    }

}
