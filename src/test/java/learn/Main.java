package learn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Main {
	
	/**
	 * 排序
	 */
	public static List sort(List<Integer> list){
		int temp =0;
		for(int i=0;i<list.size()-1;i++){
			for(int j=i+1;j<list.size();j++){
				if(list.get(i)>list.get(j)){
					temp = list.get(i);
					list.set(i,list.get(j));
					list.set(j, temp);
				}
			}
		}
		return list;
	}
	
	/*
	 * 最长上升子序列
	 */
	public int max(List<Integer> list){
		ArrayList<Integer> sortedList = new ArrayList<Integer>();
		sortedList.addAll(list);
		return 0;
	}
	public static void main(String[] args) {
		//Scanner scan = new Scanner(System.in);
		Integer[] array = {2,3,1,6,4,9,7,5,8};
		List<Integer> list = new ArrayList<Integer>();
		Collections.addAll(list, array);
		sort(list);
		for(Integer i:list){
			System.out.print(i+"  ");
		}
	}
}
