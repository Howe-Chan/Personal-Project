#insert into test_db.test_record(name,datetime,is_income,money,type_id,user_id) value('郊游', '2016-12-01', true, 30, 5, 2);
#update test_db.test_record set type_id=5 where user_id=1 and type_id=4;
#delete from test_db.test_type where user_id=1 and id=4;
#SELECT * FROM test_db.test_record;
#SELECT * FROM test_db.test_record where date_format(datetime, '%Y')='2016' and user_id=2;
#select sum(money) as a1, user_id from test_db.test_record where date_format(datetime,'%Y')='2016' and is_income=0 group by user_id;

#年结余
#select ifnull((
#select ifnull(sum(money),0) as money from test_db.test_record where user_id=2 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' and is_income = 1
# )-( 
#select ifnull(sum(money),0) as money from test_db.test_record where user_id=2 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' and is_income = 0 
#),0) as '年结余';
#类别年结余
#select ifnull((
#select ifnull(sum(money),0) as money from test_db.test_record where user_id=2 and type_id=5 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' and is_income = 1
# )-( 
#select ifnull(sum(money),0) as money from test_db.test_record where user_id=2 and type_id=5 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' and is_income = 0 
#),0) as '类别年结余';

#月结余
#select ifnull((
#select ifnull(sum(money),0) as money from test_db.test_record where user_id=2 and datetime>'2016-11-01 00:00:00' and datetime<'2016-12-01 00:00:00' and is_income = 1
# )-( 
#select ifnull(sum(money),0) as money from test_db.test_record where user_id=2 and datetime>'2016-11-01 00:00:00' and datetime<'2016-12-01 00:00:00' and is_income = 0 
#),0) as '月结余';
#类别月结余
#select ifnull((
#select ifnull(sum(money),0) as money from test_db.test_record where user_id=2 and type_id=5 and datetime>'2016-11-01 00:00:00' and datetime<'2016-12-01 00:00:00' and is_income = 1
# )-( 
#select ifnull(sum(money),0) as money from test_db.test_record where user_id=2 and type_id=5 and datetime>'2016-11-01 00:00:00' and datetime<'2016-12-01 00:00:00' and is_income = 0 
#),0) as '类别月结余';

#年支出最多的类别
#select tt.name, sum(tr.money) as money from test_db.test_record tr,test_db.test_type tt 
#where tr.type_id=tt.id and tr.user_id=2 and tr.datetime>'2016-01-01 00:00:00' and tr.datetime<'2017-01-01 00:00:00' and tr.is_income = 0 group by tr.type_id
#order by money desc limit 1;
#月支出最多的类别
select tt.name, sum(tr.money) as money from test_db.test_record tr,test_db.test_type tt 
where tr.type_id=tt.id and tr.user_id=2 and tr.datetime>'2016-11-01 00:00:00' and tr.datetime<'2016-12-01 00:00:00' and tr.is_income = 0 group by tr.type_id
order by money desc limit 1;

#按年查询所有类别
#select mond.months as datetime,ifnull(sr.money,0) as income,ifnull(zc.money,0) as expenses,ifnull(sr.money,0)-ifnull(zc.money,0) as month_surplus
#from(
#select  date_format(datetime,'%Y/%m') as months from test_db.test_record where user_id=2 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' group by months
#)as mond
#left join 
# (
#select date_format(datetime,'%Y/%m') as months,is_income as type,sum(money) as money from test_db.test_record where user_id=2 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' and is_income = 1 group by months
#)as sr
#on mond.months=sr.months
#left join(
#select date_format(datetime,'%Y/%m') as months,is_income as type,sum(money) as money from test_db.test_record where user_id=2 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' and is_income = 0 group by months
#)as zc 
#on mond.months=zc.months order by mond.months;

#按年查询单个类别查询
#通常情况下：where前面的函数是对查询出来的结果集处理；where后面的是先处理再判断得到结果集。
#所有where后面一般不使用函数，影响性能
#select mond.months as '月份',mond.tname as '类别',ifnull(sr.money,0)-ifnull(zc.money,0) as '类别结余'
#from(
#select  date_format(tr.datetime,'%Y/%m') as months,tt.name as tname from test_db.test_record tr,test_db.test_type tt where tr.type_id=tt.id and tr.user_id=2 and tr.type_id=5 and tr.datetime>'2016-01-01 00:00:00' and tr.datetime<'2017-01-01 00:00:00' group by months
#)as mond
#left join 
# (
#select date_format(datetime,'%Y/%m') as months,is_income as type,sum(money) as money from test_db.test_record where user_id=2 and type_id=5 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' and is_income = 1 group by months
#)as sr
#on mond.months=sr.months
#left join(
#select date_format(datetime,'%Y/%m') as months,is_income as type,sum(money) as money from test_db.test_record where user_id=2 and type_id=5 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00' and is_income = 0 group by months
#)as zc 
#on mond.months=zc.months order by mond.months;

#按月查询所有类别
#select date_format(tr.datetime,'%Y/%m/%d') as '日期',tr.name as '名称',tt.name as '类别',tr.is_income as '收/支',tr.money as '金额' from 
#test_db.test_record tr,test_db.test_type tt where tr.type_id=tt.id and tr.user_id = 1 and datetime>'2016-01-01 00:00:00' and datetime<'2017-01-01 00:00:00';

#按月查询单个类别
#select mond.months as '日期',mond.tname as '类别',ifnull(sr.money,0)-ifnull(zc.money,0) as '类别结余'
#from(
#select  date_format(tr.datetime,'%Y/%m/%d') as months,tt.name as tname from test_db.test_record tr,test_db.test_type tt where tr.type_id=tt.id and tr.user_id=2 and tr.type_id=5 and tr.datetime>'2016-11-01 00:00:00' and tr.datetime<'2016-12-01 00:00:00' group by months
#)as mond
#left join 
# (
#select date_format(datetime,'%Y/%m/%d') as months,is_income as type,sum(money) as money from test_db.test_record where user_id=2 and type_id=5 and datetime>'2016-11-01 00:00:00' and datetime<'2016-12-01 00:00:00' and is_income = 1 group by months
#)as sr
#on mond.months=sr.months
#left join(
#select date_format(datetime,'%Y/%m/%d') as months,is_income as type,sum(money) as money from test_db.test_record where user_id=2 and type_id=5 and datetime>'2016-11-01 00:00:00' and datetime<'2017-12-01 00:00:00' and is_income = 0 group by months
#)as zc 
#on mond.months=zc.months order by mond.months;