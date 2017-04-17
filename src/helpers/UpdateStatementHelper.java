package helpers;

import common.CatalogDB;
import common.Constants;
import common.Utils;
import datatypes.DT_Int;
import datatypes.DT_Text;
import storage.StorageManager;
import storage.model.InternalCondition;
import storage.model.DataRecord;
import storage.model.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dakle on 15/4/17.
 */
public class UpdateStatementHelper {

    public int updateSystemTablesTable(String tableName, int columnCount) {
        /*
         * System Tables Table Schema:
         * Column_no    Name                                    Data_type
         *      1       rowid                                   INT
         *      2       table_name                              TEXT
         *      3       record_count                            INT
         *      4       col_tbl_st_rowid                        INT
         *      5       nxt_avl_col_tbl_rowid                   INT
         */
        StorageManager manager = new StorageManager();
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(new InternalCondition(CatalogDB.TABLES_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, tableName));
        List<DataRecord> result = manager.findRecord(Utils.getSystemDatabasePath(), Constants.SYSTEM_TABLES_TABLENAME, conditions, true);
        if(result != null && result.size() == 0) {
            int returnValue = 1;
            Page<DataRecord> page = manager.getLastRecordAndPage(Utils.getSystemDatabasePath(), Constants.SYSTEM_TABLES_TABLENAME);
            //Check if record exists
            DataRecord lastRecord = null;
            if (page.getPageRecords().size() > 0) {
                lastRecord = page.getPageRecords().get(0);
            }
            DataRecord record = new DataRecord();
            if(lastRecord == null) {
                record.setRowId(1);
            }
            else {
                record.setRowId(lastRecord.getRowId() + 1);
            }
            record.getColumnValueList().add(new DT_Int(record.getRowId()));
            record.getColumnValueList().add(new DT_Text(tableName));
            record.getColumnValueList().add(new DT_Int(0));
            if(lastRecord == null) {
                record.getColumnValueList().add(new DT_Int(1));
                record.getColumnValueList().add(new DT_Int(columnCount + 1));
            }
            else {
                DT_Int startingColumnIndex = (DT_Int) lastRecord.getColumnValueList().get(4);
                returnValue = startingColumnIndex.getValue();
                record.getColumnValueList().add(new DT_Int(returnValue));
                record.getColumnValueList().add(new DT_Int(returnValue + columnCount));
            }
            record.populateSize();
            if(manager.writeRecord(Utils.getSystemDatabasePath(), Constants.SYSTEM_TABLES_TABLENAME, record)) {
                conditions.clear();
                conditions.add(new InternalCondition(CatalogDB.TABLES_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, Constants.SYSTEM_TABLES_TABLENAME));
                List<Byte> updateColumnsIndexList = new ArrayList<>();
                updateColumnsIndexList.add(CatalogDB.TABLES_TABLE_SCHEMA_RECORD_COUNT);
                List<Object> updateValueList = new ArrayList<>();
                updateValueList.add(new DT_Int(1));
                manager.updateRecord(Utils.getSystemDatabasePath(), Constants.SYSTEM_TABLES_TABLENAME, conditions, updateColumnsIndexList, updateValueList, true);
            }
            return returnValue;
        }
        else {
            System.out.println("Table already exists!");
            return -1;
        }
    }

    public boolean updateSystemColumnsTable(String tableName, int startingRowId, List<String> columnNames, List<String> columnDataType, List<String> columnKeyConstraints, List<String> columnNullConstraints) {
        /*
         * System Tables Table Schema:
         * Column_no    Name                                    Data_type
         *      1       rowid                                   INT
         *      2       table_name                              TEXT
         *      3       column_name                             TEXT
         *      4       data_type                               TEXT
         *      5       column_key                              TEXT
         *      6       ordinal_position                        TINYINT
         *      7       is_nullable                             TEXT
         */
        StorageManager manager = new StorageManager();
        if(columnNames.size() != columnDataType.size() && columnDataType.size() != columnKeyConstraints.size() && columnKeyConstraints.size() != columnNullConstraints.size()) return false;
        int i = 0;
        for(; i < columnNames.size(); i++) {
            DataRecord record = new DataRecord();
            record.setRowId(startingRowId++);
            record.getColumnValueList().add(new DT_Int(record.getRowId()));
            record.getColumnValueList().add(new DT_Text(tableName));
            record.getColumnValueList().add(new DT_Text(columnNames.get(i)));
            record.getColumnValueList().add(new DT_Text(columnDataType.get(i)));
            record.getColumnValueList().add(new DT_Text(columnKeyConstraints.get(i)));
            record.getColumnValueList().add(new DT_Int(i + 1));
            record.getColumnValueList().add(new DT_Text(columnNullConstraints.get(i)));
            record.populateSize();
            if (!manager.writeRecord(Utils.getSystemDatabasePath(), Constants.SYSTEM_COLUMNS_TABLENAME, record)) {
                break;
            }
        }
        if(i > 0) {
            List<InternalCondition> conditions = new ArrayList<>();
            conditions.add(new InternalCondition(CatalogDB.TABLES_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, Constants.SYSTEM_COLUMNS_TABLENAME));
            List<Byte> updateColumnsIndexList = new ArrayList<>();
            updateColumnsIndexList.add(CatalogDB.TABLES_TABLE_SCHEMA_RECORD_COUNT);
            List<Object> updateValueList = new ArrayList<>();
            updateValueList.add(new DT_Int(i));
            manager.updateRecord(Utils.getSystemDatabasePath(), Constants.SYSTEM_TABLES_TABLENAME, conditions, updateColumnsIndexList, updateValueList, true);
        }
        return true;
    }

}