package com.mv.schelokov.car_rent.model.db.repository;

import com.mv.schelokov.car_rent.model.db.repository.exceptions.CriteriaMismatchException;
import com.mv.schelokov.car_rent.model.db.repository.interfaces.AbstractSqlRepository;
import com.mv.schelokov.car_rent.model.db.repository.interfaces.Criteria;
import com.mv.schelokov.car_rent.model.db.repository.interfaces.SqlCriteria;
import com.mv.schelokov.car_rent.model.entities.InvoiceLine;
import com.mv.schelokov.car_rent.model.entities.builders.InvoiceTypeBuilder;
import com.mv.schelokov.car_rent.model.entities.builders.InvoiceLineBuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class InvoiceLineRepository extends AbstractSqlRepository<InvoiceLine> {
    
    public interface ReadCriteria extends SqlCriteria {}

    public interface DeleteCriteria extends SqlCriteria {}

    public static class FindByInvoiceId implements ReadCriteria {
        private static final String QUERY = "SELECT line_id,invoice_id,details,"
                + "type,name,amount FROM invoice_lines_full WHERE invoice_id=?";
        private static final int INVOICE_ID_COLUMN = 1;
        private final int invoiceId;

        public FindByInvoiceId(int invoiceId) {
            this.invoiceId = invoiceId;
        }

        @Override
        public String toSqlQuery() {
            return QUERY;
        }

        @Override
        public void setStatement(PreparedStatement ps) throws SQLException {
            ps.setInt(INVOICE_ID_COLUMN, invoiceId);
        }
    }
    
    private static final String CREATE_QUERY = "INSERT INTO invoice_lines ("
            + "invoice_id,details,type,amount) VALUES (?,?,?,?)";
    private static final String REMOVE_QUERY = "DELETE FROM invoice_lines WHERE"
            + " line_id=?";
    private static final String UPDATE_QUERY = "UPDATE invoice_lines SET "
            + "invoice_id=?,details=?,type=?,amount=? WHERE line_id=?";

    /**
     * The Field enum has column names for read methods and number of column for
     * the update method and the add method (in the NUMBER attribute)
     */
    enum Fields {
        LINE_ID(5), INVOICE_ID(1), DETAILS(2), TYPE(3), NAME, AMOUNT(4);
        
        int NUMBER;
        
        Fields(int number) {
            this.NUMBER = number;
        }
        
        Fields() {
        }
    }

    public InvoiceLineRepository(Connection connection) {
        super(connection);
    }
    
    @Override
    protected String getCreateQuery() {
        return CREATE_QUERY;
    }

    @Override
    protected String getRemoveQuery() {
        return REMOVE_QUERY;
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected InvoiceLine createItem(ResultSet rs) throws SQLException {
        return new InvoiceLineBuilder()
                .setId(rs.getInt(Fields.LINE_ID.name()))
                .setInvoiceId(rs.getInt(Fields.INVOICE_ID.name()))
                .setDetails(rs.getString(Fields.DETAILS.name()))
                .setType(new InvoiceTypeBuilder()
                    .setId(rs.getInt(Fields.TYPE.name()))
                    .setName(rs.getString(Fields.NAME.name()))
                    .getInvoceType())
                .setAmount(rs.getInt(Fields.AMOUNT.name()))
                .getInvoiceLine();
    }
    
    @Override
    protected void setStatement(PreparedStatement ps, InvoiceLine item, 
            boolean isUpdateStatement) throws SQLException {
        ps.setInt(Fields.INVOICE_ID.NUMBER, item.getInvoiceId());
        ps.setString(Fields.DETAILS.NUMBER, item.getDetails());
        ps.setInt(Fields.TYPE.NUMBER, item.getType().getId());
        ps.setInt(Fields.AMOUNT.NUMBER, item.getAmount());

        if (isUpdateStatement)
            ps.setInt(Fields.LINE_ID.NUMBER, item.getId());
    }

    @Override
    protected boolean checkCriteriaInstance(Criteria criteria, 
            boolean isDeleteCriteria) throws CriteriaMismatchException {
        if (isDeleteCriteria) {
            if (criteria instanceof DeleteCriteria)
                return true;
        } else if (criteria instanceof ReadCriteria)
            return true;
        throw new CriteriaMismatchException();
    }
    
}