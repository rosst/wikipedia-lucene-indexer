package org.ross.turner.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryCLI {

	private static final String QUERYOPTION = "query";

	private static final String MAXRESOPTION = "maxRes";
	
	private static final String INDEXOPTION = "index";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IndexerCLI.class);

	public static void main(String[] args) {

		CommandLineParser parser = new BasicParser();

		// add options

		Options options = new Options();

		Option maxResOpt = OptionBuilder.withArgName(MAXRESOPTION)
				.withLongOpt(MAXRESOPTION).hasArg()
				.withDescription("the maximum number of results to return").isRequired(true).create();

		options.addOption(maxResOpt);
		
		Option indexOpt = OptionBuilder.withArgName(INDEXOPTION)
				.withLongOpt(INDEXOPTION).hasArg()
				.withDescription("the index file").isRequired(true).create();
		
		options.addOption(indexOpt);

		HelpFormatter formatter = new HelpFormatter();

		CommandLine line = null;

		try {
			
			line = parser.parse(options, args);

		} catch (org.apache.commons.cli.ParseException e) {

			System.err.println("Invalid input arguments: " + e.getMessage());

			formatter.printHelp(QueryCLI.class.getName(), options);

			System.exit(2);

		}
		
		IndexSearcher searcher = null;
		
		try {
			
			String indexVal = line.getOptionValue(INDEXOPTION);
			
			searcher = new IndexSearcher(DirectoryReader.open(FSDirectory
					.open(new File(indexVal))));
		
		} catch (IOException e) {

			LOGGER.error("Error reading index:" + e.getMessage());
			
			System.exit(2);
			
		}
		
		System.out.println("Enter a search query or \"q\" to exit");
		
		Scanner in = new Scanner(System.in);
		
		int maxResults = 0;
		
		try{
		
			maxResults = Integer.parseInt(line.getOptionValue(MAXRESOPTION));
			
		}catch(NumberFormatException e){
			
			LOGGER.error("Invalid number max results, defaulting to 10");
			
		}
		
		while(in.hasNext()){
		
			String q = in.next();
			
			if(q.equals("q")){
				
				System.exit(0);
				
			}
			
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LATEST);
			
			Query query;
			
			try {
				
				query = new QueryParser(Version.LATEST, "title", analyzer).parse(q);
				
				TopDocs docs = searcher.search(query, maxResults);
				
				for(int i = 0; i < docs.scoreDocs.length; i++){
					
					System.out.println(output(searcher.doc(docs.scoreDocs[i].doc)));
					
				}
				
			} catch (ParseException e1) {
				
				LOGGER.error("Unable to parse query");
				
			} catch (IOException e) {
				
				LOGGER.error("Error searching for query: " + q);
			
			}
			
			System.out.println("Search again? Enter query or \"q\" to exit");
			
		}
	
	}
	
	private static String output(Document doc){
		
		StringBuilder buf = new StringBuilder();
		
		buf.append(doc.getField("title").name() + " : ");
		
		buf.append(doc.getField("title").stringValue() + " : \n");

		buf.append(doc.getField("path").name() + " : ");
		
		buf.append(doc.getField("path").stringValue() + " : \n");
		
		return buf.toString();
		
	}
	
}
