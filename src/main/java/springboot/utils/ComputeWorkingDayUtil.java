package springboot.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @ClassName ComputeWorkingDayUtil，获取每年的法定节假日和调休日
 * @Description
 * @Author zdd
 * @Date 2024/12/4 16:39
 **/
@Component
public class ComputeWorkingDayUtil {

    private static final Logger log = LoggerFactory.getLogger(ComputeWorkingDayUtil.class);
    //定义两个List，一个存放节假日日期，另一个存放调休的工作日期
    private static List<String> HOLIDAY_LIST = new ArrayList<>();
    private static List<String> SPECIAL_WORKDAY_LIST = new ArrayList<>();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //静态代码块内调用第三方接口拿到数据存进List中
    static {
        Map<String, Object> param = new HashMap<String, Object>() {{
            //这里key值是注册天行API账号给的   天行网站申请的api-key  https://www.tianapi.com/
            put("key", "3be1d21d5cfd010732d629b01264d21f");
            // 1按年、2按月、3范围
            put("type", 1);
        }};

        //获取当前年份，循环调用3次，拿到3年的数据
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        param.put("date", currentYear);
        String url = "http://api.tianapi.com/jiejiari/index";
        String response = HttpUtil.get(url, param);
        JSONObject resObj = JSONUtil.parseObj(response);
        int code = (int) resObj.get("code");
        if (code == 200) {
            JSONArray newslist = (JSONArray) resObj.get("newslist");
            for (Object listObj : newslist) {
                JSONObject obj = (JSONObject) listObj;
                String holidays = (String) obj.get("vacation");
                String[] holidayArray = holidays.split("\\|");
                HOLIDAY_LIST.addAll(Arrays.asList(holidayArray));
                String remark = (String) obj.get("remark");
                if (StringUtils.isNotEmpty(remark)) {
                    String[] special = remark.split("\\|");
                    SPECIAL_WORKDAY_LIST.addAll(Arrays.asList(special));
                }
            }
        } else {
            log.info("调用第三方天行api接口获取节假日信息失败，请注意年底前维护次年节假日和调休日数据！");
            // 如果请求第三方网站api接口数据失败，则需要   每年12月份左右等国务院公告  提前手动录入每年的法定节假日和调休日
            String holidays = "2022-01-01,2022-01-02,2022-01-03,2022-01-31,2022-02-01,2022-02-02,2022-02-03,2022-02-04,2022-02-05,2022-02-06," +
                    "2022-04-03,2022-04-04,2022-04-05,2022-04-30,2022-05-01,2022-05-02,2022-05-03,2022-05-04,2022-06-03,2022-06-04,2022-06-05," +
                    "2022-09-10,2022-09-11,2022-09-12,2022-10-01,2022-10-02,2022-10-03,2022-10-04,2022-10-05,2022-10-06,2022-10-07";
            String specialWorkdays = "2022-01-29,2022-01-30,2022-04-02,2022-04-24,2022-05-27,2022-10-08,2022-10-09";

            // 转化为数组
            String[] holidaysArr = holidays.split(",");
            String[] specialWorkdaysArr = specialWorkdays.split(",");
            HOLIDAY_LIST = new ArrayList<>(Arrays.asList(holidaysArr));
            SPECIAL_WORKDAY_LIST = new ArrayList<>(Arrays.asList(specialWorkdaysArr));
        }
    }

    //计算工作日数的方法
    public static int computeWorkingDays(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTime(start);
        endCal.setTime(end);

        int workDays = 0;

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(end);
            endCal.setTime(start);
        }

        while (startCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
            String current = DateUtil.format(startCal.getTime(), "yyyy-MM-dd");
            int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                if (!HOLIDAY_LIST.contains(current)) {
                    System.out.println(true);
                    workDays++;
                }
            }
            if (SPECIAL_WORKDAY_LIST.contains(current)) {
                System.out.println(true);
                workDays++;
            }
            startCal.add(Calendar.DATE, 1);
        }

        return workDays;
    }

    /**
     * 获取n个工作日后的日期
     *
     * @param today opening date
     * @param num   num个工作日后
     * @return
     * @throws ParseException
     */
    public static Date getScheduleActiveDate(Date today, int num) throws ParseException {
        int delay = 1;
        while (delay <= num) {
            Date tomorrow = getTomorrow(today);
            String tomorrowStr = sdf.format(tomorrow);
            //当前日期+1,判断是否是节假日,不是的同时要判断是否是周末,都不是则scheduleActiveDate日期+1
            if ((!isWeekend(tomorrow) && !isHoliday(tomorrowStr))
                    || isExtraWorkdays(tomorrowStr)) {
                delay++;
                today = tomorrow;
            } else if (isWeekend(tomorrow)) {
                today = tomorrow;
            } else if (isHoliday(tomorrowStr)) {
                today = tomorrow;
            }
        }

        return today;
    }

    /**
     * 获取n个工作日前的日期
     *
     * @param today opening date
     * @param num   num个工作日后
     * @return
     * @throws ParseException
     */
    public static Date getScheduleActiveDateBefore(Date today, int num){
        int delay = 1;
        while (delay <= num) {
            Date yesterday = getYesterday(today);
            String yesterdayStr = sdf.format(yesterday);
            //当前日期+1,判断是否是节假日,不是的同时要判断是否是周末,都不是则scheduleActiveDate日期+1
            if ((!isWeekend(yesterday) && !isHoliday(yesterdayStr))
                    || isExtraWorkdays(yesterdayStr)) {
                delay++;
                today = yesterday;
            } else if (isWeekend(yesterday)) {
                today = yesterday;
            } else if (isHoliday(yesterdayStr)) {
                today = yesterday;
            }
        }

        return today;
    }

    /**
     * 获取tomorrow的日期
     *
     * @param date
     * @return
     */
    public static Date getTomorrow(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 获取yesterday的日期
     *
     * @param date
     * @return
     */
    public static Date getYesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 判断是否是weekend
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static boolean isWeekend(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 判断是否是holiday
     *
     * @param sdate
     * @return
     * @throws ParseException
     */
    public static boolean isHoliday(String sdate) {
        for (String s : HOLIDAY_LIST) {
            if (sdate.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是由于放假需要额外工作的周末
     *
     * @param sdate
     * @return
     * @throws ParseException
     */
    public static boolean isExtraWorkdays(String sdate) {
        for (String s : SPECIAL_WORKDAY_LIST) {
            if (sdate.equals(s)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断给定的日期是否为工作日
     *
     * @param date 待判断的日期
     * @return 如果是工作日返回true，否则返回false
     */
    public static Boolean isWorkingDay(Date date) {
        LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return isWorkingDay(localDate);
    }

    public static Boolean isWorkingDay(LocalDateTime date) {
        String dateStr = date.format(df);
        // 是否加班日
        if (SPECIAL_WORKDAY_LIST.contains(dateStr)) {
            return true;
        }
        // 是否节假日
        if (HOLIDAY_LIST.contains(dateStr)) {
            return false;
        }
        // 如果是1-5表示周一到周五，是工作日
        DayOfWeek week = date.getDayOfWeek();
        if (week == DayOfWeek.SATURDAY || week == DayOfWeek.SUNDAY) {
            return false;
        }
        return true;
    }


}
