/**
 * Copyright (C) 2011 Akiban Technologies Inc.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package com.akiban.sql.unparser;

import com.akiban.sql.TestBase;

import com.akiban.sql.compiler.AISBinder;
import com.akiban.sql.compiler.BoundNodeToString;
import com.akiban.sql.parser.SQLParser;
import com.akiban.sql.parser.StatementNode;

import com.akiban.ais.ddl.SchemaDef;
import com.akiban.ais.ddl.SchemaDefToAis;
import com.akiban.ais.model.AkibanInformationSchema;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Collection;

@RunWith(Parameterized.class)
public class BoundNodeToStringTest extends TestBase
{
  public static final File RESOURCE_DIR =
    new File(NodeToStringTest.RESOURCE_DIR, "bound");

  protected SQLParser parser;
  protected BoundNodeToString unparser;
  protected AISBinder binder;

  @Before
  public void before() throws Exception {
    parser = new SQLParser();
    unparser = new BoundNodeToString();
    unparser.setUseBindings(true);

    String sql = fileContents(new File(RESOURCE_DIR, "schema.ddl"));
    SchemaDef schemaDef = SchemaDef.parseSchema("use user; " + sql);
    SchemaDefToAis toAis = new SchemaDefToAis(schemaDef, false);
    AkibanInformationSchema ais = toAis.getAis();
    binder = new AISBinder(ais, "user");
  }

  @Parameters
  public static Collection<Object[]> statements() throws Exception {
    return sqlAndExpected(RESOURCE_DIR);
  }

  public BoundNodeToStringTest(String caseName, String sql, String expected) {
    super(caseName, sql, expected);
  }

  @Test
  public void testBound() throws Exception {
    StatementNode stmt = parser.parseStatement(sql);
    binder.bind(stmt);
    assertEquals(expected, unparser.toString(stmt));
  }

}