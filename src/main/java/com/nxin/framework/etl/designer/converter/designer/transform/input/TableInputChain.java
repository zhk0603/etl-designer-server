package com.nxin.framework.etl.designer.converter.designer.transform.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertFactory;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.enums.DatasourceType;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class TableInputChain extends TransformConvertChain {
    private static final ThreadLocal<TableInputMeta> tableInputMeta = ThreadLocal.withInitial(TableInputMeta::new);
    private static final ThreadLocal<String> previousStep = ThreadLocal.withInitial(String::new);

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "TableInputMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            int databaseId = (int) formAttributes.get("datasource");
            String sql = (String) formAttributes.get("sql");
            boolean lazyConversionActive = (boolean) formAttributes.get("lazyConversionActive");
            boolean executeEachInputRow = (boolean) formAttributes.get("executeEachInputRow");
            boolean variableReplacementActive = (boolean) formAttributes.get("variableReplacementActive");
            previousStep.set((String) formAttributes.get("lookupFromStepValue"));
            int rowLimit = (int) formAttributes.get("rowLimit");
            tableInputMeta.get().setSQL(sql);
            tableInputMeta.get().setLazyConversionActive(lazyConversionActive);
            tableInputMeta.get().setExecuteEachInputRow(executeEachInputRow);
            tableInputMeta.get().setVariableReplacementActive(variableReplacementActive);
            tableInputMeta.get().setRowLimit(String.valueOf(rowLimit));

            Datasource datasource = datasourceService.one((long) databaseId);
            DatabaseMeta databaseMeta = new DatabaseMeta(datasource.getName(), DatasourceType.getValue(datasource.getCategory()), "JDBC", datasource.getHost(), datasource.getSchemaName(), datasource.getPort().toString(), datasource.getUsername(), Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(datasource.getPassword()));
            databaseMeta.setStreamingResults(datasource.getUseCursor());
            Properties properties = new Properties();
            if (datasource.getUsePool()) {
                databaseMeta.setUsingConnectionPool(true);
                if (datasource.getPoolInitialSize() != null) {
                    databaseMeta.setInitialPoolSize(datasource.getPoolInitialSize());
                }
                if (datasource.getPoolMaxSize() != null) {
                    databaseMeta.setMaximumPoolSize(datasource.getPoolMaxSize());
                }
                if (datasource.getPoolInitial() != null) {
                    properties.put("initialSize", "" + datasource.getPoolInitial());
                }
                if (datasource.getPoolMaxActive() != null) {
                    properties.put("maxActive", "" + datasource.getPoolMaxActive());
                }
                if (datasource.getPoolMaxIdle() != null) {
                    properties.put("maxIdle", "" + datasource.getPoolMaxIdle());
                }
                if (datasource.getPoolMinIdle() != null) {
                    properties.put("minIdle", "" + datasource.getPoolMinIdle());
                }
                if (datasource.getPoolMaxWait() != null) {
                    properties.put("maxWait", "" + datasource.getPoolMaxWait());
                }
            }
            String parameters = datasource.getParameter();

            if (StringUtils.hasLength(parameters)) {
                List<Map<String, Object>> params = objectMapper.readValue(parameters, new TypeReference<List<Map<String, Object>>>() {
                });
                for (Map<String, Object> param : params) {
                    for (Map.Entry<String, Object> entry : param.entrySet()) {
                        if (StringUtils.hasLength(entry.getKey()) && !ObjectUtils.isEmpty(entry.getValue())) {
                            databaseMeta.addExtraOption(DatasourceType.getValue(datasource.getCategory()), entry.getKey(), (String) entry.getValue());
                            properties.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                databaseMeta.setConnectionPoolingProperties(properties);
            }
            tableInputMeta.get().setDatabaseMeta(databaseMeta);
            StepMeta stepMeta = new StepMeta(stepName, tableInputMeta.get());
            TransformConvertFactory.getTransformConvertChains().add(this);
            if (formAttributes.containsKey("distribute")) {
                boolean distribute = (boolean) formAttributes.get("distribute");
                stepMeta.setDistributes(distribute);
            }
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            return new ResponseMeta(cell.getId(), stepMeta, databaseMeta);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {
        tableInputMeta.get().setLookupFromStep(transMeta.findStep(idNameMapping.get(previousStep.get())));
        tableInputMeta.remove();
        previousStep.remove();
    }
}
