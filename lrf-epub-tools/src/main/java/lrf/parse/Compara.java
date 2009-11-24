package lrf.parse;

import java.util.Comparator;

import lrf.objects.BBObj;

public class Compara implements Comparator<BBObj> {

	@Override
	public int compare(BBObj o1, BBObj o2) {
		if(o1.printPosition==0 && o2.printPosition==0)
			return 0;
		if(o1.printPosition==0)
			return 1;
		if(o2.printPosition==0)
			return -1;
		return o1.printPosition-o2.printPosition;
	}

}
