import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        port=3306,
        user="root",
        password="20226093",
        database="clinic"
    )
    cursor = conn.cursor()
    cursor.execute("SHOW TABLES LIKE 'chat_sessions'")
    result = cursor.fetchone()
    if result:
        print("Table 'chat_sessions' EXISTS.")
    else:
        print("Table 'chat_sessions' DOES NOT exist.")
    cursor.close()
    conn.close()
except Exception as e:
    print("Error:", e)
