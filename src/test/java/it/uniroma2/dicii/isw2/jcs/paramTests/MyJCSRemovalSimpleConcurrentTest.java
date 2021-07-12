package it.uniroma2.dicii.isw2.jcs.paramTests;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class MyJCSRemovalSimpleConcurrentTest {

    private JCS jcs;
    private String configFilename;  //public static void setConfigFilename(String configFilename)
    private String region;  //public static JCS getInstance(String region)
    private int count;

    public MyJCSRemovalSimpleConcurrentTest(String configFilename,String region, int count){
        this.configFilename = configFilename;
        this.region = region;
        this.count = count;
    }

    @Parameterized.Parameters
    public static Collection<?> getParameter() {

        return Arrays.asList(new Object[][] {
                {"/TestRemoval.ccf" ,"testCache1", 500},
                {"/TestRemoval.ccf" ,"testCache1", -1},
                {"/TestRemoval.ccf" ,"testCache1", 0}


        });
    }



    /**
     * Test setup
     */
    @Before
    public void configure() throws CacheException {
        JCS.setConfigFilename( this.configFilename );
        jcs = JCS.getInstance( this.region );
    }





    /**
     * Verify that 2 level deep hierchical removal works.
     *
     * @throws Exception
     */
    @Test
    public void testTwoDeepRemoval() throws CacheException {
        System.out.println( "------------------------------------------" );
        System.out.println( "testTwoDeepRemoval" );


        for ( int i = 0; i <= count; i++ )
        {
            jcs.put( "key:" + i + ":anotherpart", "data" + i );
        }

        for ( int i = count; i >= 0; i-- )
        {
            String res = (String) jcs.get( "key:" + i + ":anotherpart" );
            if ( res == null )
            {
                assertNotNull( "[key:" + i + ":anotherpart] should not be null, " + jcs.getStats(), res );
            }
        }
        System.out.println( "Confirmed that " + count + " items could be found" );

        for ( int i = 0; i <= count; i++ )
        {
            jcs.remove( "key:" + i + ":" );
            assertNull( jcs.getStats(), jcs.get( "key:" + i + ":anotherpart" ) );
        }
        System.out.println( "Confirmed that " + count + " items were removed" );

        System.out.println( jcs.getStats() );
    }


    /**
     * Verify that 1 level deep hierchical removal works.
     *
     * @throws Exception
     */
    @Test
    public void testSingleDepthRemoval()
            throws Exception
    {

        System.out.println( "------------------------------------------" );
        System.out.println( "testSingleDepthRemoval" );


        for ( int i = 0; i <= count; i++ )
        {
            jcs.put( i + ":key", "data" + i );
        }

        for ( int i = count; i >= 0; i-- )
        {
            String res = (String) jcs.get( i + ":key" );
            if ( res == null )
            {
                assertNotNull( "[" + i + ":key] should not be null", res );
            }
        }
        System.out.println( "Confirmed that " + count + " items could be found" );

        for ( int i = 0; i <= count; i++ )
        {
            jcs.remove( i + ":" );
            assertNull( jcs.get( i + ":key" ) );
        }
        System.out.println( "Confirmed that " + count + " items were removed" );

        System.out.println( jcs.getStats() );

    }


    /**
     * Verify that clear removes everyting as it should.
     *
     * @throws Exception
     */
    @Test
    public void testClear()
            throws Exception
    {

        System.out.println( "------------------------------------------" );
        System.out.println( "testRemoveAll" );

        for ( int i = 0; i <= count; i++ )
        {
            jcs.put( i + ":key", "data" + i );
        }

        for ( int i = count; i >= 0; i-- )
        {
            String res = (String) jcs.get( i + ":key" );
            if ( res == null )
            {
                assertNotNull( "[" + i + ":key] should not be null", res );
            }
        }
        System.out.println( "Confirmed that " + count + " items could be found" );

        System.out.println( jcs.getStats() );

        jcs.clear();

        for ( int i = count; i >= 0; i-- )
        {
            String res = (String) jcs.get( i + ":key" );
            if ( res != null )
            {
                assertNull( "[" + i + ":key] should be null after remvoeall" + jcs.getStats(), res );
            }
        }
        System.out.println( "Confirmed that all items were removed" );

    }


    /**
     * Verify that we can clear repeatedly without error.
     *
     * @throws Exception
     */
    @Test
    public void testClearRepeatedlyWithoutError()
            throws Exception
    {

        System.out.println( "------------------------------------------" );
        System.out.println( "testRemoveAll" );

        jcs.clear();

        for ( int i = 0; i <= count; i++ )
        {
            jcs.put( i + ":key", "data" + i );
        }

        for ( int i = count; i >= 0; i-- )
        {
            String res = (String) jcs.get( i + ":key" );
            if ( res == null )
            {
                assertNotNull( "[" + i + ":key] should not be null", res );
            }
        }
        System.out.println( "Confirmed that " + count + " items could be found" );

        System.out.println( jcs.getStats() );

        for ( int i = count; i >= 0; i-- )
        {
            jcs.put( i + ":key", "data" + i );
            jcs.clear();
            String res = (String) jcs.get( i + ":key" );
            if ( res != null )
            {
                assertNull( "[" + i + ":key] should be null after remvoeall" + jcs.getStats(), res );
            }
        }
        System.out.println( "Confirmed that all items were removed" );

    }
}