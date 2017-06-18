package me.superckl.biometweakercore.util;

public class CollectionHelper {

	public static <T> int find(final T toFind, final T[] in){
		for(int i = 0; i < in.length; i++)
			if((in[i] == toFind) || in[i].equals(toFind))
				return i;
		return -1;
	}

}
