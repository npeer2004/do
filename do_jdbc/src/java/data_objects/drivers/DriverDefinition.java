package data_objects.drivers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.jruby.Ruby;
import org.jruby.RubyObjectAdapter;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.builtin.IRubyObject;

import data_objects.RubyType;

/**
 *
 * @author alexbcoles
 */
public interface DriverDefinition {

    public String getModuleName();

    public String getErrorName();

    public URI parseConnectionURI(IRubyObject uri) throws URISyntaxException,
            UnsupportedEncodingException;

    public RaiseException newDriverError(Ruby runtime, String message);

    public RaiseException newDriverError(Ruby runtime, SQLException sqe);

    public RaiseException newDriverError(Ruby runtime, SQLException sqle,
            Statement statement);

    public RubyObjectAdapter getObjectAdapter();

    public IRubyObject getTypecastResultSetValue(Ruby runtime, ResultSet rs,
            int col, RubyType type) throws SQLException, IOException;

    public void setPreparedStatementParam(PreparedStatement ps,
            IRubyObject arg, int idx) throws SQLException;

    /**
     * Callback for registering output parameter
     * Necessary for Oracle INSERT ... RETURNING ... INTO ... statements
     *
     * @return true if output parameter was registered
     */
    public boolean registerPreparedStatementReturnParam(String sqlText, PreparedStatement ps, int idx) throws SQLException;

    /**
     * Get registered return parameter
     * Necessary for Oracle INSERT ... RETURNING ... INTO ... statements
     *
     * @return return parameter (long value)
     */
    public long getPreparedStatementReturnParam(PreparedStatement ps) throws SQLException;

    /**
     * Callback for doing driver specific SQL statement modification
     * Necessary for Oracle driver to replace :insert_id with ?
     *
     * @param SqlText
     * @param args
     * @return a SQL Text formatted for preparing a PreparedStatement
     */
    public String prepareSqlTextForPs(String sqlText, IRubyObject[] args);

    /**
     * Whether the Driver supports properly supports JDBC 3.0's
     * autogenerated keys feature
     *
     * @return
     */
    public boolean supportsJdbcGeneratedKeys();

    /**
     * Whether the Driver supports properly JDBC 2.0's
     * scrollable result sets
     *
     * XXX left taking consideration into further versions
     *
     * @return
     */
    public boolean supportsJdbcScrollableResultSets();

    /**
     * A workaround for drivers that throw a SQLException if Connection#prepareStatement(String, int)
     * is called.
     *
     * @return
     */
    public boolean supportsConnectionPrepareStatementMethodWithGKFlag();

    /**
     * Method tells if Calendar instances passed to PreparedStatement
     * are accepted (returned true) or ignored
     * @return
     */
    public boolean supportsCalendarsInJDBCPreparedStatement();

    /**
     * Whether the Driver supports specifying a connection encoding
     *
     * @return
     */
    public boolean supportsConnectionEncodings();

    /**
     * If the driver does not properly support JDBC 3.0's autogenerated keys,
     * then custom SQL can be provided to look up the autogenerated keys for
     * a connection.
     *
     * @param connection
     * @return
     */
    public ResultSet getGeneratedKeys(Connection connection);

    /**
     * A default list of properties for a connection for a driver.
     *
     * @return
     */
    public Properties getDefaultConnectionProperties();

    /**
     * Callback for setting connection properties after connection is established.
     *
     * @return
     */
    public void afterConnectionCallback(Connection connection) throws SQLException;

    /**
     * If the driver supports setting connection encodings, specify the appropriate
     * property to set the connection encoding.
     *
     * @param props
     * @param encodingName
     * @see #supportsConnectionEncodings()
     */
    public void setEncodingProperty(Properties props, String encodingName);

    public String quoteString(String str);

    public String toString(PreparedStatement ps);

}
