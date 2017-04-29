import java.util.*;

public class MultiThreadSorting {
	static String Sorting(String str)
	{
		String[] tokens = str.split(", ");
		int[] arr;
		arr = new int[tokens.length];
		int count = 0;
		for (String token:tokens)
		{
			arr[count++] = Integer.parseInt(token);
		}
		
		Arrays.sort(arr);
		
		String output_str="";
		for (int i=0; i<count; i++)
		{
			output_str += arr[i];
			if(i != count-1)
				output_str += ", ";
			else
				;
		}
		
		return output_str;
	}
}