package com.alicloud.openservices.tablestore.timestream.model.query;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.ClientException;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.timestream.internal.TableMetaGenerator;
import com.alicloud.openservices.tablestore.timestream.internal.Utils;
import com.alicloud.openservices.tablestore.timestream.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 单时间线查询
 */
public class MetaGetter {
    static Logger logger = LoggerFactory.getLogger(MetaFilter.class);

    private AsyncClient asyncClient;
    private String metaTableName;
    private TimestreamIdentifier identifier;
    private boolean returnAll = false;
    private List<String> attrToGet = null;

    public MetaGetter(AsyncClient asyncClient,
                      String metaTableName, TimestreamIdentifier identifier) {
        this.asyncClient = asyncClient;
        this.metaTableName = metaTableName;
        this.identifier = identifier;
    }

    /**
     * 设置需要查询的attributes列名
     * @param columns
     * @return
     */
    public MetaGetter selectAttributes(String... columns) {
        if (this.returnAll) {
            throw new ClientException("returnAll has been set.");
        }
        this.attrToGet = Arrays.asList(columns);
        return this;
    }

    /**
     * 获取查询的attributes列名
     * @return
     */
    public List<String> getAttributesToSelect() {
        return this.attrToGet;
    }

    /**
     * 设置查询完整的TimestreamMeta
     * @return
     */
    public MetaGetter returnAll() {
        if (this.attrToGet != null) {
            throw new ClientException("Attributes to select has been set.");
        }
        this.returnAll = true;
        return this;
    }

    /**
     * 是否查询完整的TimestreamMeta
     * @return
     */
    public boolean isReturnAll() {
        return this.returnAll;
    }

    /**
     * 查询
     * @return
     */
    public TimestreamMeta fetch() {
        SingleRowQueryCriteria rowQuery = new SingleRowQueryCriteria(
                metaTableName, Utils.convertIdentifierToPK(identifier).build());
        rowQuery.setMaxVersions(1);
        GetRowRequest request = new GetRowRequest(rowQuery);
        if (this.returnAll) {
            // pass
        } else if (this.attrToGet != null){
            rowQuery.addColumnsToGet(this.attrToGet);
            rowQuery.addColumnsToGet(TableMetaGenerator.CN_TAMESTAMP_NAME);
        } else {
            rowQuery.addColumnsToGet(TableMetaGenerator.CN_TAMESTAMP_NAME);
        }
        GetRowResponse response;
        try {
            response = this.asyncClient.getRow(request, null).get();
        } catch (InterruptedException e) {
            throw new ClientException(String.format("The thread was interrupted: %s", e.getMessage()));
        } catch (ExecutionException e) {
            throw new ClientException("The thread was aborted", e);
        }
        if (response.getRow() == null) {
            return null;
        }
        return Utils.deserializeTimestreamMeta(response.getRow());
    }
}
