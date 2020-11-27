package com.luee.wally.constants;

import java.util.ArrayList;
import java.util.Collection;

import com.luee.wally.command.PackageURLGroup;

public class FBPackageNameConstants {


	private static final String API_KEY_B = "QuN5chUnh2cONoLJRB9oI8tu2bqrOhVqatvBZOzFQaepM-7pAHaSPSLR29GQsmFQd9cBXZRz94mV2uIC9tfTJ_";
	private static final String API_KEY_A = "wZ2EZPalZxgPKVHk7CmcPn4ekCDTz1itbl699gviba6w4OeCaPp_Atev4sR2u9vTu_d2gXlG6sBBFrNrEAqO2S";
	private static final String API_KEY_C = "u1RvBTe6CkOz4Cr9V0JdVktg1cJrWqTOrvI1Sx7eeWciiRsDRAsyvicxJxmrlEDa7VbaRkD4ErnyGd9ip0fhiH";
	
	private static String[] packageNamesA = { "com.adp.gamebox" };

	private static String[] packageNamesB = { "com.relaxingbraintraining.knives", "com.relaxingbraintraining.cookiejellymatch",
			"com.relaxingbraintraining.raccoonbubbles", "com.relaxingbraintraining.popslice",
			"com.relaxingbraintraining.blocks", "com.relaxingbraintraining.wordcup",
			"com.relaxingbraintraining.hexapuzzle", "com.relaxingbraintraining.dunk",
			"com.relaxingbraintraining.rollthatball", "com.relaxingbraintraining.idleemojis",
			"com.relaxingbraintraining.solitairekingdom", "com.relaxingbraintraining.zenpuzzle",
			"com.relaxingbraintraining.pixelpaint", "com.relaxingbraintraining.ballrush",
			"com.relaxingbraintraining.oneline", "com.relaxingbraintraining.sudokumaster",
			"com.relaxingbraintraining.mergecandy", "com.relaxingbraintraining.grindmygears",
			"com.relaxingbraintraining.mousekeeper", "com.relaxingbraintraining.logicblocks",
			"com.relaxingbraintraining.zombiechallenge", "com.relaxingbraintraining.six",
			"com.relaxingbraintraining.emojibounce", "com.relaxingbraintraining.brickmania",
			"com.relaxingbraintraining.colorpuzzle", "com.relaxingbraintraining.colorjump",
			"com.relaxingbraintraining.numbermerge", "com.relaxingbraintraining.pipeout",
			 "com.relaxingbraintraining.unblockbar","com.relaxingbraintraining.pixelcolor",
			"com.relaxingbraintraining.masterofsudoku",
			"com.relaxingbraintraining.planes" };

	
	 
		
	
	private static String[] packageNamesC = { "com.moregames.makemoney", "com.coinmachine.app",
			"com.matchmine.app" };
	
	public static Collection<PackageURLGroup> packageURLGroups=new ArrayList<>();
	
	static{
		
		for (String packageName : packageNamesA) {
	        PackageURLGroup group=new PackageURLGroup("https://r.applovin.com/max/fbUserAdRevenueReport?api_key=", API_KEY_A, packageName);	
	        packageURLGroups.add(group);
		}
		for (String packageName : packageNamesB) {
			PackageURLGroup group=new PackageURLGroup("https://r.applovin.com/max/fbUserAdRevenueReport?api_key=", API_KEY_B, packageName);
			packageURLGroups.add(group);	
		}
		for (String packageName : packageNamesC) {
			PackageURLGroup group=new PackageURLGroup("https://r.applovin.com/max/fbUserAdRevenueReport?api_key=", API_KEY_C, packageName);	
			packageURLGroups.add(group);		
		}		
	}
}
