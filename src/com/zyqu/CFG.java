package com.zyqu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

//dump the call graph from FlowDroid
public class CFG {
	public CFG(){}
	
	//output the call graph to JSON formate
	private static String dumpCallGraph(CallGraph cg){
		Iterator<Edge> itr = cg.iterator();
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		while(itr.hasNext()){
			Edge e = itr.next();
			String srcSig = e.getSrc().toString();
			String destSig = e.getTgt().toString();
			Set<String> neighborSet;
			if(map.containsKey(srcSig)){
				neighborSet = map.get(srcSig);
			}else{
				neighborSet = new HashSet<String>();
			}
			neighborSet.add(destSig);
			map.put(srcSig, neighborSet );
			
		}
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(map);
		return json;
	}
	
	private static void printUsage(){
		System.out.println("Incorrect arguments: [0] = apk-file, [1] = android-jar-directory");		
	}
	
	public static void main(String[] args){
		if (args.length < 2){
			printUsage();
			return;
		}
		
		String apkPath = args[0];
		String androidJarPath = args[1];
		
		File apkFile = new File(apkPath);
		String extension = apkFile.getName().substring(apkFile.getName().lastIndexOf("."));
		if (!extension.equals(".apk") || !apkFile.exists()){
			System.out.println("apk-file not exists "+ apkFile.getName());
			return;
		}

		
		File sdkFile = new File(androidJarPath);
		if (!sdkFile.exists()){
			System.out.println("android-jar-directory not exists "+ sdkFile.getName());
			return;			
		}
		
		
		Path curDir = Paths.get(System.getProperty("user.dir"));
		Path sourceSinkPath = Paths.get(curDir.toString(), "SourcesAndSinks.txt");
		File sourceSinkFile = sourceSinkPath.toFile();
		if (!sourceSinkFile.exists()){
			System.out.println("SourcesAndSinks.txt not exists");
			return;				
		}
		
		
		SetupApplication app = new SetupApplication(androidJarPath, apkPath);
		try {
			app.calculateSourcesSinksEntrypoints(sourceSinkFile.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		soot.G.reset();
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_process_dir(Collections.singletonList(apkPath));
		Options.v().set_android_jars(androidJarPath);
		Options.v().set_whole_program(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_output_format(Options.output_format_none);
		Options.v().setPhaseOption("cg.spark", "on");
		Scene.v().loadNecessaryClasses(); 
		SootMethod entryPoint = app.getEntryPointCreator().createDummyMain();
		Options.v().set_main_class(entryPoint.getSignature());
		Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
		System.out.println(entryPoint.getActiveBody());
		PackManager.v().runPacks();
		System.out.println("Call graph size: "+ Scene.v().getCallGraph().size());		
		String res = dumpCallGraph(Scene.v().getCallGraph());

		//where the JSON file is outputed 
		Path outputPath = Paths.get(curDir.toString(), "static-cfg-"+ apkFile.getName().substring(0, apkFile.getName().lastIndexOf(".")));
		
		File out = outputPath.toFile();
		try {
			if(out.exists()){
				out.delete();
			}
			FileWriter fw = new FileWriter(out);
			fw.write(res);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Dump to file: "+outputPath);
		System.out.println("Finish");
		
				
	}
}
