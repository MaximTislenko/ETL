/*
chcp 65001 && spark-shell -i C:\Users\User1\Documents\Linux\Less_02_01.scala --conf "spark.driver.extraJavaOptions=-Dfile.encoding=utf-8"
*/
//import org.apache.spark.internal.Logging
import org.apache.spark.sql.functions.{col, collect_list, concat_ws}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.expressions.Window

var df1 = spark.read
.option("delimeter", ",")
.option("header", "true")
//.option("encoding", "windows-1251")
.csv("C:/Users/User1/Documents/Linux/Geekbrains_ETL/s2_data.csv")

df1.show()

df1=df1
.withColumn("children", col("children"). cast("int"))
.withColumn("days_employed", col("days_employed"). cast("float"))
.withColumn("total_income", col("total_income"). cast("float")).dropDuplicates()
.withColumn("purpose_category",
				when(col("purpose").like("%Р°РІС‚Рѕ%"), "РѕРїРµСЂР°С†РёРё СЃ Р°РІС‚РѕРјРѕР±РёР»РµРј")
				when(col("purpose").like("%РЅРµРґРІРёР¶%")|| col("purpose").like("%Р¶РёР»СЊ%"),"РѕРїРµСЂР°С†РёРё СЃ РЅРµРґРІРёР¶РёРјРѕСЃС‚СЊСЋ")
			)
//.otherwise("Unknown")
.withColumn("total_income2", 
			when(col("total_income").isNotNull, col("total_income"))
			.otherwise(avg("total_income").over(Window.partitionBy("income_type").orderBy("income_type"))))
.withColumn("total_income2", col("total_income2"). cast("float"))

df1.write.format("jdbc").option("url", "jdbc:mysql://localhost:3306/spark?user=root&password=X!7y2wSa")
.option("driver", "com.mysql.cj.jdbc.Driver")
.option("dbtable", "tasketl2a")
.mode("overwrite").save()

df1.show()

val s = df1.columns.map(c => sum (col(c).isNull.cast("integer")).alias(c))
val df2 = df1.agg(s.head, s.tail:_*)
val t = df2.columns.map(c => df2.select(lit(c).alias("col_name"), col(c).alias("null_count")))
df2.show()
val df_agg_col = t.reduce((df1, df2) => df1.union(df2))
df_agg_col.show()











/*import java.sql._;

var q = """
CREATE TABLE `tasketl1u` (
`Р¤Р°РјРёР»РёСЏ СЃС‚СѓРґРµРЅС‚Р°` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
`РРјСЏ СЃС‚СѓРґРµРЅС‚Р°` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
`РљРѕРґ СЃС‚СѓРґРµРЅС‚Р°` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
INDEX `РљРѕРґ СЃС‚СѓРґРµРЅС‚Р°` (`РљРѕРґ СЃС‚СѓРґРµРЅС‚Р°`(100)) USING BTREE
)
COMMENT='Р›СЋР±РѕР№ С‚РµРєСЃС‚_РїРѕС‡С‚Рё'
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB;
"""

		
def sqlexecute(sql: String) = {
	var conn: Connection = null;
	var stmt: Statement = null;
	try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/spark?user=root&password=X!7y2wSa");
		stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		println(sql+" complete");
		} catch {
			case e: Exception => println("exception caught: " + e);
			}
					
		}
		
sqlexecute(q)
*/		
		
System.exit(0)
