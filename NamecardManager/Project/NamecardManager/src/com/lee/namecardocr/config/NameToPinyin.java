package com.lee.namecardocr.config;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 姓名转拼音
 * @author chan
 *
 */
public class NameToPinyin {
	
	// 特殊姓氏
	private static Map<String, String> specialSurname = new HashMap<String, String>();
	static {
		specialSurname.put("秘", "毕");
		specialSurname.put("褚", "楚");
		specialSurname.put("藉", "及");
		specialSurname.put("适", "阔");
		specialSurname.put("句", "钩");
		specialSurname.put("阚", "看");
		specialSurname.put("繁", "婆");
		specialSurname.put("乜", "聂");
		specialSurname.put("仇", "求");
		specialSurname.put("朴", "瓢");
		specialSurname.put("单", "善");
		specialSurname.put("眭", "虽");
		specialSurname.put("洗", "显");
		specialSurname.put("员", "运");
		specialSurname.put("祭", "债");
		specialSurname.put("查", "扎");
		specialSurname.put("宿", "速");
		specialSurname.put("乘", "成");
		specialSurname.put("长", "涨");
		specialSurname.put("辟", "敝");
		specialSurname.put("车", "砗");
		specialSurname.put("谷", "古");
		specialSurname.put("可", "刻");
		specialSurname.put("会", "快");
		specialSurname.put("铅", "迁");
		specialSurname.put("茄", "癿");
		specialSurname.put("食", "四");
		specialSurname.put("万", "腕");
		specialSurname.put("尉", "卫");
		specialSurname.put("吾", "吴");
		specialSurname.put("贾", "甲");
		specialSurname.put("沈", "审");
		specialSurname.put("翟", "宅");
		specialSurname.put("曾", "增");
		specialSurname.put("晟", "成");
		specialSurname.put("乐", "月");
		specialSurname.put("区", "欧");
		specialSurname.put("冯", "逢");
		specialSurname.put("石", "时");
		specialSurname.put("柏", "摆");
		specialSurname.put("缪", "秒");
		specialSurname.put("牟", "谬");
		specialSurname.put("贲", "犇");
		specialSurname.put("宁", "侫");
		specialSurname.put("薄", "博");
		specialSurname.put("都", "嘟");
		specialSurname.put("莘", "深");
		specialSurname.put("殷", "音");
		specialSurname.put("颉", "杰");
		specialSurname.put("解", "懈");
		specialSurname.put("隽", "绢");
		specialSurname.put("奇", "骑");
		specialSurname.put("宓", "符");
		specialSurname.put("盖", "葛");
		specialSurname.put("覃", "秦");
		specialSurname.put("谌", "陈");
		specialSurname.put("召", "哨");
		specialSurname.put("隗", "韦");
		specialSurname.put("种", "虫");
	}

	/**
	 * 将姓名转换成拼音
	 * @param name 姓名
	 * @return
	 */
	public static String convertNameToPinyin(String name) {
		String temp = "";
		// 截取姓
		String surname = name.substring(0, 1);
		// 截取名
		String monicker = name.substring(1, name.length());
		// 查询多音字姓氏
		String replaceSurname = specialSurname.get(surname) != null ? specialSurname.get(surname) : surname;
		// 汉字转拼音
		try {
			HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
			/*
			 * HanyuPinyinCaseType.LOWERCASE	小写
			 * HanyuPinyinCaseType.UPPERCASE	大写
			 */
			format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			/*
			 * HanyuPinyinToneType.WITH_TONE_NUMBER		数字声调（lü3）
			 * HanyuPinyinToneType.WITHOUT_TONE			没有声调（lü）
			 * HanyuPinyinToneType.WITH_TONE_MARK		符号声调（lǚ）
			 */
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			/*
			 * HanyuPinyinVCharType.WITH_U_AND_COLON	ü表示成u:
			 * HanyuPinyinVCharType.WITH_V				ü表示成v
			 * HanyuPinyinVCharType.WITH_U_UNICODE		ü表示成ü
			 */
			format.setVCharType(HanyuPinyinVCharType.WITH_V);
			/*
			 * 转换单个中文字符
			 * PinyinHelper.toHanyuPinyinStringArray(char ch, HanyuPinyinOutputFormat outputFormat)
			 * char ch									被转换的中文字符
			 * HanyuPinyinOutputFormat outputFormat		输出格式
			 */
//			String[] singleWord = PinyinHelper.toHanyuPinyinStringArray('吕', format);
			/*
			 * 转换整行混合字符
			 * PinyinHelper.toHanYuPinyinString(String arg0, HanyuPinyinOutputFormat arg1, String arg2, boolean arg3)
			 * String arg0						被转换的混合字符
			 * HanyuPinyinOutputFormat arg1		输出格式
			 * String arg2						每个拼音后加上的字符<使用该字符进行分离每个中文字符的拼音，非中文字符不进行分离>
			 * boolean arg3						结果中是否出现非中文字符<true:出现 false:隐藏>
			 */
//			String lineMixWord = PinyinHelper.toHanYuPinyinString("English:Mr.Lv,How are you?中文：吕先生，你好吗？", format, " ", true);
			temp = PinyinHelper.toHanYuPinyinString(replaceSurname + monicker, format, "", true);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			/*
			 * 当HanyuPinyinToneType为WITH_TONE_MARK，同时HanyuPinyinVCharType为WITH_U_AND_COLON、WITH_V的时候，抛出此异常
			 */
			e.printStackTrace();
		}
		return temp;
	}
}
