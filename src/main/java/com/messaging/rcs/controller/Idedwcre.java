package com.messaging.rcs.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.aspectj.weaver.ast.And;

public class Idedwcre {

	public static void main(String[] args) {
//		int pageSize = 5;
//		int start = 6;
//		int pageNum = start != 0 ? start / pageSize : 0;
//
		
//		System.out.println("pageNum : "+ pageNum);
		
//		String Start="7525";
//		String end ="7850";
//	
//		DeletepartitionfromleadinfoDetail(Start,end);
		
		int[] nums = {1,7,9,2,5};
		
		countofFairNumber(nums,11,11);
	}

	
	public static void DeletepartitionfromleadinfoDetail(String Start, String End) {
			
		System.out.println("ALTER TABLE rcsmessaging.lead_info_detail");
		System.out.print("DROP PARTITION ");
		for(int i = Integer.valueOf(Start);i<Integer.valueOf(End)+1;i++)
		{
			System.out.print(" p"+i);
			if(i!=Integer.valueOf(End)) {
				System.out.print(",");
			}
			else {
				System.out.print(";");
			}
			if(i%10==0) {
				System.out.println();
			}
		}
	}
//	(0,3), (0,4), (0,5), (1,3), (1,4), and (1,5)
//	 {0,1,7,4,4,5}
//	lower 3
//	upper 6
	
	
//	Given a 0-indexed integer array nums of size n and two integers lower and upper, return the number of fair pairs.
//
//			A pair (i, j) is fair if:
//
//			0 <= i < j < n, and
//			lower <= nums[i] + nums[j] <= upper
	
	
	public static void countofFairNumber(int[] nums, int lower, int upper) {

		for(int i =0; i <nums.length;i++)
		{
			for(int j =0; j <nums.length;j++)
			{
				int k = nums[i] + nums[j] ;
				if (i<j && i>=0 && lower<=k && upper>=k ) {
					System.out.println("("+ i+","+j+")");
				}
			}
		}
		
		
	}
}
