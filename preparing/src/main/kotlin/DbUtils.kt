package org.jyutping.preparing

import java.sql.Connection
import java.sql.PreparedStatement

/**
 * Execute batched inserts with a shared BATCH_SIZE and transactional semantics.
 * Returns the number of rows inserted.
 */
fun <T> batchInsert(connection: Connection, insertSql: String, rows: Iterable<T>, bind: (PreparedStatement, T) -> Unit): Int {
    connection.autoCommit = false
    var inserted = 0
    try {
        connection.prepareStatement(insertSql).use { statement ->
            var batchCount = 0
            for (row in rows) {
                bind(statement, row)
                statement.addBatch()
                inserted++
                batchCount++
                if (batchCount >= BATCH_SIZE) {
                    statement.executeBatch()
                    statement.clearBatch()
                    batchCount = 0
                }
            }
            if (batchCount > 0) {
                statement.executeBatch()
                statement.clearBatch()
            }
        }
        connection.commit()
    } catch (e: Exception) {
        connection.rollback()
        throw e
    } finally {
        connection.autoCommit = true
    }
    return inserted
}
