package com.liangzhmj.cat.tools.num;

/**
 * 整数位标记工具类
 * @author liangzhmj
 *
 */
@SuppressWarnings("unused")
public class IntegerFlagUtils {


	/**
	 * 设置位标记
	 * @param obj
	 * @param flagIndex
	 * @param flag
	 * @return
	 */
	public static int setBitflag(int obj, int flagIndex, boolean flag) {
		int temp = 1;
		temp = temp << flagIndex;// 使到temp的后四位可能为0001,0010...
		if (flag) {
			obj |= temp;// 确保aclState对应temp为1的位为1，表示授予权限
		} else {
			obj &= ~temp;// ~temp表示按位取反~temp可能为1110,1101,1011,0111，
			// 这样确保aclState对应~temp为0的位为0，表示不授予权限
		}
		return obj;
	}

	/**
	 * 获取位标记
	 * @param obj
	 * @param flagIndex
	 * @return
	 */
	public static int getBitflag(int obj,int flagIndex) {
		int temp = 1;
		temp = temp << flagIndex;//确保flagIndex为1
		temp &= obj;//如果obj的flagIndex也为1，则temp!=0
		if (temp != 0) {
			return 1;
		}
		return 0;
	}

}
