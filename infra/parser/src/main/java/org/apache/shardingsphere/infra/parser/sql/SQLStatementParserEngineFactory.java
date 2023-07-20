/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.parser.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.database.spi.DatabaseType;
import org.apache.shardingsphere.sql.parser.api.CacheOption;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL statement parser engine factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SQLStatementParserEngineFactory {
    
    private static final Map<String, SQLStatementParserEngine> ENGINES = new ConcurrentHashMap<>();
    
    /**
     * Get SQL statement parser engine.
     *
     * @param databaseType database type
     * @param sqlStatementCacheOption SQL statement cache option
     * @param parseTreeCacheOption parse tree cache option
     * @param isParseComment is parse comment
     * @return SQL statement parser engine
     */
    public static SQLStatementParserEngine getSQLStatementParserEngine(final DatabaseType databaseType,
                                                                       final CacheOption sqlStatementCacheOption, final CacheOption parseTreeCacheOption, final boolean isParseComment) {
        SQLStatementParserEngine result = ENGINES.get(databaseType.getType());
        if (null == result) {
            result = ENGINES.computeIfAbsent(databaseType.getType(), key -> new SQLStatementParserEngine(key, sqlStatementCacheOption, parseTreeCacheOption, isParseComment));
        } else if (!result.getSqlStatementCacheOption().equals(sqlStatementCacheOption) || !result.getParseTreeCacheOption().equals(parseTreeCacheOption)
                || result.isParseComment() != isParseComment) {
            result = new SQLStatementParserEngine(databaseType.getType(), sqlStatementCacheOption, parseTreeCacheOption, isParseComment);
            ENGINES.put(databaseType.getType(), result);
        }
        return result;
    }
}
