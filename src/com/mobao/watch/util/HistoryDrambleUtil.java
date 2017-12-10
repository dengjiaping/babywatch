package com.mobao.watch.util;

import java.util.ArrayList;

import com.mb.zjwb1.R;

public class HistoryDrambleUtil {

	private static ArrayList<Integer> historyDramble;

	static {
		historyDramble = new ArrayList<Integer>();
		int history1 = R.drawable.history1;
		int history2 = R.drawable.history2;
		int history3 = R.drawable.history3;
		int history4 = R.drawable.history4;
		int history5 = R.drawable.history5;
		int history6 = R.drawable.history6;
		int history7 = R.drawable.history7;
		int history8 = R.drawable.history8;
		int history9 = R.drawable.history9;
		int history10 = R.drawable.history10;
		int history11 = R.drawable.history11;
		int history12 = R.drawable.history12;
		int history13 = R.drawable.history13;
		int history14 = R.drawable.history14;
		int history15 = R.drawable.history15;
		int history16 = R.drawable.history16;
		int history17 = R.drawable.history17;
		int history18 = R.drawable.history18;
		int history19 = R.drawable.history19;
		int history20 = R.drawable.history20;
		int history21 = R.drawable.history21;
		int history22 = R.drawable.history22;
		int history23 = R.drawable.history23;
		int history24 = R.drawable.history24;
		int history25 = R.drawable.history25;
		int history26 = R.drawable.history26;
		int history27 = R.drawable.history27;
		int history28 = R.drawable.history28;
		int history29 = R.drawable.history29;
		int history30 = R.drawable.history30;
		int history31 = R.drawable.history31;
		int history32 = R.drawable.history32;
		int history33 = R.drawable.history33;
		int history34 = R.drawable.history34;
		int history35 = R.drawable.history35;
		int history36 = R.drawable.history36;
		int history37 = R.drawable.history37;
		int history38 = R.drawable.history38;
		int history39 = R.drawable.history39;
		int history40 = R.drawable.history40;
		int history41 = R.drawable.history41;
		int history42 = R.drawable.history42;
		int history43 = R.drawable.history43;
		int history44 = R.drawable.history44;
		int history45 = R.drawable.history45;
		int history46 = R.drawable.history46;
		int history47 = R.drawable.history47;
		int history48 = R.drawable.history48;
		int history49 = R.drawable.history49;
		int history50 = R.drawable.history50;
		
		historyDramble.add(history1);
		historyDramble.add(history2);
		historyDramble.add(history3);
		historyDramble.add(history4);
		historyDramble.add(history5);
		historyDramble.add(history6);
		historyDramble.add(history7);
		historyDramble.add(history8);
		historyDramble.add(history9);
		historyDramble.add(history10);
		historyDramble.add(history11);
		historyDramble.add(history12);
		historyDramble.add(history13);
		historyDramble.add(history14);
		historyDramble.add(history15);
		historyDramble.add(history16);
		historyDramble.add(history17);
		historyDramble.add(history18);
		historyDramble.add(history19);
		historyDramble.add(history20);
		historyDramble.add(history21);
		historyDramble.add(history22);
		historyDramble.add(history23);
		historyDramble.add(history24);
		historyDramble.add(history25);
		historyDramble.add(history26);
		historyDramble.add(history27);
		historyDramble.add(history28);
		historyDramble.add(history29);
		historyDramble.add(history30);
		historyDramble.add(history31);
		historyDramble.add(history32);
		historyDramble.add(history33);
		historyDramble.add(history34);
		historyDramble.add(history35);
		historyDramble.add(history36);
		historyDramble.add(history37);
		historyDramble.add(history38);
		historyDramble.add(history39);
		historyDramble.add(history40);
		historyDramble.add(history41);
		historyDramble.add(history42);
		historyDramble.add(history43);
		historyDramble.add(history44);
		historyDramble.add(history45);
		historyDramble.add(history46);
		historyDramble.add(history47);
		historyDramble.add(history48);
		historyDramble.add(history49);
		historyDramble.add(history50);
	}

	public static int getHistotyDramble(int index) {
		if(index>=50){
			return  historyDramble.get(50);
		}
		return historyDramble.get(index);
	}
}
