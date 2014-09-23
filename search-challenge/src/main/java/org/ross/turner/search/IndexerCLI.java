package org.ross.turner.search;

import java.io.File;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *<p>
 * Command line interface for indexing Wikipedia articles.
 * 
 * Options as follows
 * 
 *</p>
 */
public class IndexerCLI 
{
    private static final String INPUTOPTION = "input";
    
    private static final String OUTPUTOPTION = "output";
    
    private static final String NUMTHREADSOPTION = "numThreads";
	
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerCLI.class);
    
    public static void main( String[] args )
    {
        
    	CommandLineParser parser = new BasicParser();
        
        // add options          
        
        Options options = new Options();
        
        Option intOpt = OptionBuilder.withArgName(INPUTOPTION).withLongOpt(INPUTOPTION).hasArg().withDescription("the input wikipedia aritcle file").isRequired(true).create();

        options.addOption(intOpt);
        
        Option sumOpt = OptionBuilder.withArgName(OUTPUTOPTION).withLongOpt(OUTPUTOPTION).hasArg().withDescription("the output path").isRequired(true).create();
        
        options.addOption(sumOpt);
        
        Option siteOpt = OptionBuilder.withArgName(INPUTOPTION).withLongOpt(NUMTHREADSOPTION).hasArg().withDescription("the number of threads to use for indexing (default is 1)").isRequired(false).create();
        
        options.addOption(siteOpt);

        HelpFormatter formatter = new HelpFormatter();
        
        CommandLine line = null;
        
        try {
                
                line = parser.parse( options, args );
        
        } catch (org.apache.commons.cli.ParseException e) {
        
        		e.printStackTrace();
        	
                System.err.println("Invalid input arguments");
                
                formatter.printHelp(IndexerCLI.class.getName(), options);
                
                System.exit(2);
        
        }
        
        File articles = new File(line.getOptionValue(INPUTOPTION));
        
        if(!articles.exists()){
        	
        	outputFileArgumentErrorAndExit(articles.getAbsolutePath(),formatter,options);
        	
        }
        
        LOGGER.info("Reading articles from " + articles.getAbsolutePath());
        
        File output = new File(line.getOptionValue(OUTPUTOPTION));
        
        LOGGER.info("Using output path " + output.getAbsolutePath());
        
        int numThreads = 1;
        
        try{
        
        	numThreads = Integer.valueOf(line.getOptionValue(NUMTHREADSOPTION));
        
        }catch(NumberFormatException e){
        	
        	outputInvalidNumThreadsErrorAndExit(line.getOptionValue(NUMTHREADSOPTION),formatter,options);
        	
        }
        
        if(numThreads < 1){
        	
        	numThreads = 1;
        
        }

        LOGGER.info("Using " + numThreads + " threads");
        
    }
    
    public IndexerCLI(){
    	
    }
    
    
    private static void outputInvalidNumThreadsErrorAndExit(String numThreads, HelpFormatter formatter, Options options){
        
        System.err.println("Invalid" + NUMTHREADSOPTION +" argument: " + numThreads + ", argument should be a number >= 1");
        
        formatter.printHelp(IndexerCLI.class.getName(), options);
        
        System.exit(2);
        
    }
    
    private static void outputFileArgumentErrorAndExit(String path, HelpFormatter formatter, Options options){
        
        System.err.println("Path does not exist: " + path);
        
        formatter.printHelp(IndexerCLI.class.getName(), options);
        
        System.exit(2);
    }
       
}