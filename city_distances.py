# Mapeo de nombres de ciudades a IDs según la información proporcionada por el usuario
city_ids = {
    "Granada": 1,
    "Madrid": 2,
    "Barcelona": 3,
    "Sevilla": 4,
    "Valencia": 5,
    "Málaga": 6,
    "Zaragoza": 7,
    "Murcia": 8,
    "Bilbao": 9,
    "Córdoba": 10,
    "Alicante": 11,
    "Valladolid": 12,
    "Vigo": 13,
    "Gijón": 14
}

# Crear una lista de tuplas con las distancias entre las ciudades
distances = []
for i in range(len(cities_data)):
    for j in range(i+1, len(cities_data.columns)-1):
        city1 = cities_data.columns[j+1]
        city2 = cities_data.iloc[i, 0]
        distance = cities_data.iloc[i, j+1]
        if pd.notna(distance):
            distances.append((city_ids[city1], city_ids[city2], distance))

# Generar la instrucción SQL
sql_insert = "INSERT INTO distances (city1_id, city2_id, distance) VALUES\n"
sql_values = ["({}, {}, {})".format(city1_id, city2_id, dist) for city1_id, city2_id, dist in distances]
sql_query = sql_insert + ",\n".join(sql_values) + ";"

sql_query[:1000]
