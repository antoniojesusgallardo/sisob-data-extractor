/*
    Copyright (c) 2014 "(IA)2 Research Group. Universidad de Málaga"
                        http://iaia.lcc.uma.es | http://www.uma.es

    This file is part of SISOB Data Extractor.

    SISOB Data Extractor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SISOB Data Extractor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SISOB Data Extractor. If not, see <http://www.gnu.org/licenses/>.
*/
package eu.sisob.uma.extractors.adhoc.email;

import eu.sisob.uma.api.concurrent.threadpoolutils.CallbackableTaskExecution;
import eu.sisob.uma.api.concurrent.threadpoolutils.CallbackableTaskPoolExecutor;
import java.util.logging.Logger;


public class EmailExtractorService
{    
    private static final Logger LOG = Logger.getLogger(EmailExtractorService.class.getName());
    
    //static final String LOG_NAME = "WebSearcherExtractor";
    
    private static EmailExtractorService INSTANCE = null;
    
    private CallbackableTaskPoolExecutor ctpe;      
    
    /**
     *      
     */
    private EmailExtractorService()
    {
        ctpe = new CallbackableTaskPoolExecutor(5, 100);      
        
        //ProjectLogger.LOGGER = Logger.getLogger(LOG_NAME);
    }	        
    
    /**
     *
     */
    public synchronized static void createInstance() 
    {
        if (INSTANCE == null) 
        {             
            INSTANCE = new EmailExtractorService();
        }
    }
 
    /**
     *
     * @return
     */
    public static EmailExtractorService getInstance() 
    {
        if (INSTANCE == null) 
        {
            createInstance();
        }
        return INSTANCE;
    }
    
    /**
     * 
     * @param cte
     * @throws InterruptedException
     */
    public void addExecution(CallbackableTaskExecution cte) throws InterruptedException
    {   
        ctpe.runTask(cte);
    }
    
    /**
     * End crawler manager and crawler
     */
    public static void releaseInstance()
    {                   
        if(INSTANCE != null)
        {
            INSTANCE = null;
        }
    }
}
