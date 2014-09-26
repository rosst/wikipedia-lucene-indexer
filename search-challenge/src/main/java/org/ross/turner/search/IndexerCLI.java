package org.ross.turner.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.benchmark.utils.ExtractWikipedia;
import org.apache.lucene.document.Document;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Command line interface for indexing Wikipedia articles.
 * 
 * usage: org.ross.turner.search.IndexerCLI:
 * <li>
 * <ul>
 * --input <input> the input wikipedia aritcle file
 * </ul>
 * <ul>
 * --output <output> the output path
 * </ul>
 * </li>
 * </p>
 */
public class IndexerCLI {
	
	private static final String INPUTOPTION = "input";

	private static final String OUTPUTOPTION = "output";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IndexerCLI.class);

	public static void main(String[] args) {

		CommandLineParser parser = new BasicParser();

		// add options

		Options options = new Options();

		Option intOpt = OptionBuilder.withArgName(INPUTOPTION)
				.withLongOpt(INPUTOPTION).hasArg()
				.withDescription("the input wikipedia aritcle file")
				.isRequired(true).create();

		options.addOption(intOpt);

		Option outOpt = OptionBuilder.withArgName(OUTPUTOPTION)
				.withLongOpt(OUTPUTOPTION).hasArg()
				.withDescription("the output path").isRequired(true).create();

		options.addOption(outOpt);

		HelpFormatter formatter = new HelpFormatter();

		CommandLine line = null;

		try {

			line = parser.parse(options, args);

		} catch (org.apache.commons.cli.ParseException e) {

			System.err.println("Invalid input arguments");

			formatter.printHelp(IndexerCLI.class.getName(), options);

			System.exit(2);

		}

		File articles = new File(line.getOptionValue(INPUTOPTION));

		if (!articles.exists()) {

			outputFileArgumentErrorAndExit(articles.getAbsolutePath(),
					formatter, options);

		}

		LOGGER.info("Reading articles from " + articles.getAbsolutePath());

		File output = new File(line.getOptionValue(OUTPUTOPTION));

		LOGGER.info("Using output path " + output.getAbsolutePath());

		File dumpOutput = new File(output.getAbsolutePath() + File.separator
				+ "dump");

		String extractArgs[] = new String[] { "-i", articles.getAbsolutePath(),
				"-o", dumpOutput.getAbsolutePath(), "-d" };

		try {

			ExtractWikipedia.main(extractArgs);

		} catch (Exception e) {

			LOGGER.error("Error extracting articles: " + e.getMessage());

			System.exit(1);

		}

		FSDirectory fs = null;

		try {

			LOGGER.info("Indexing extracted articles in "
					+ dumpOutput.getAbsolutePath());

			fs = FSDirectory.open(output);

		} catch (IOException e) {

			LOGGER.error("Error reading extracted articles: " + e.getMessage());

			System.exit(1);

		}

		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LATEST);

		LOGGER.info("Using " + analyzer.getClass().getName()
				+ " for tokenisation");

		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,
				analyzer);

		config.setOpenMode(OpenMode.CREATE);

		config.setRAMBufferSizeMB(256);
		
		IndexWriter indexWriter = null;

		try {

			indexWriter = new IndexWriter(fs, config);
			
			File dir = new File(dumpOutput.getAbsolutePath());

			Iterator<File> files = FileUtils.iterateFiles(dir, null, true);

			int filesIndexed = 0; 
			
			while(files.hasNext()) {

				File file = files.next();
				
				if(file.isDirectory()){
					
					continue;
					
				}
				
				Document document = new Document();

				String path = file.getAbsolutePath();

				BufferedReader reader = new BufferedReader(new FileReader(file));

				String nextLine = null;

				int offset = 1;

				Document doc = new Document();
				
				doc.add(new TextField("path", path, Store.YES));
				
				while ((nextLine = reader.readLine()) != null) {

					switch (offset) {
						
						case 5: doc.add(new TextField("body", nextLine, Store.NO));
						
						case 4: break;
						
						case 3: doc.add(new TextField("title", nextLine, Store.YES));
						
						case 2: break;
						
						case 1: break;
						
						default: break;
						
					}
					
					offset++;

				}
				
				indexWriter.addDocument(document);
				
				filesIndexed++;
				
			}
			
			LOGGER.info("No. of files to indexex: " + filesIndexed);
			
			indexWriter.commit();
			
			indexWriter.close();

		} catch (IOException e) {

			LOGGER.error("Error writing the index: " + e.getMessage());

			System.exit(1);

		}

		LOGGER.info("Indexing finished");

	}

	private static void outputFileArgumentErrorAndExit(String path,
			HelpFormatter formatter, Options options) {

		System.err.println("Path does not exist: " + path);

		formatter.printHelp(IndexerCLI.class.getName(), options);

		System.exit(2);
	}

}
