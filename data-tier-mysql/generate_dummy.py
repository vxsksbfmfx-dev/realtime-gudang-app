from faker import Faker
import random

fake = Faker('id_ID')

TOTAL_DATA = 100000
CHUNK_SIZE = 10000
FILE_NAME = 'dummy_barang.sql'

barang_list = [
    # Elektronik
    "Kabel Charger USB", "Adaptor Fast Charging", "Power Bank 10000mAh",
    "Mouse Wireless", "Keyboard Mechanical", "Headset Bluetooth", "Lampu LED 12W",

    # ATK
    "Buku Tulis", "Pulpen Standard", "Pensil 2B", "Spidol Whiteboard",
    "Penghapus", "Kertas A4 80gsm",

    # Makanan & Minuman
    "Air Mineral 600ml", "Teh Botol Sosro", "Kopi Sachet",
    "Mie Instan Goreng", "Susu UHT",

    # Pakaian & Aksesoris
    "Kaos Polos", "Kemeja Lengan Panjang", "Jaket Hoodie",
    "Topi Baseball", "Tas Ransel", "Dompet Kulit",

    # Peralatan Rumah
    "Sapu Lantai", "Pel Lantai", "Ember Plastik",
    "Wajan Anti Lengket", "Rice Cooker Mini", "Dispenser Air"
]

kategori_map = {
    "Kabel Charger USB": "Elektronik",
    "Adaptor Fast Charging": "Elektronik",
    "Power Bank 10000mAh": "Elektronik",
    "Mouse Wireless": "Elektronik",
    "Keyboard Mechanical": "Elektronik",
    "Headset Bluetooth": "Elektronik",
    "Lampu LED 12W": "Elektronik",

    "Buku Tulis": "ATK",
    "Pulpen Standard": "ATK",
    "Pensil 2B": "ATK",
    "Spidol Whiteboard": "ATK",
    "Penghapus": "ATK",
    "Kertas A4 80gsm": "ATK",

    "Air Mineral 600ml": "Minuman",
    "Teh Botol Sosro": "Minuman",
    "Kopi Sachet": "Minuman",
    "Mie Instan Goreng": "Makanan",
    "Susu UHT": "Minuman",

    "Kaos Polos": "Pakaian",
    "Kemeja Lengan Panjang": "Pakaian",
    "Jaket Hoodie": "Pakaian",
    "Topi Baseball": "Aksesoris",
    "Tas Ransel": "Aksesoris",
    "Dompet Kulit": "Aksesoris",

    "Sapu Lantai": "Peralatan Rumah",
    "Pel Lantai": "Peralatan Rumah",
    "Ember Plastik": "Peralatan Rumah",
    "Wajan Anti Lengket": "Peralatan Rumah",
    "Rice Cooker Mini": "Peralatan Rumah",
    "Dispenser Air": "Peralatan Rumah"
}

with open(FILE_NAME, 'w', encoding='utf-8') as f:
    count = 0
    while count < TOTAL_DATA:
        f.write(
            "INSERT INTO barang (nama_barang, kategori, stok, tanggal_masuk) VALUES\n"
        )

        rows = []
        for _ in range(CHUNK_SIZE):
            if count >= TOTAL_DATA:
                break

            nama_barang = random.choice(barang_list)
            kategori = kategori_map[nama_barang]
            stok = random.randint(0, 500)
            tanggal_masuk = fake.date_between('-2y', 'today').strftime('%Y-%m-%d')

            rows.append(
                f"('{nama_barang}', '{kategori}', {stok}, '{tanggal_masuk}')"
            )
            count += 1

        f.write(",\n".join(rows) + ";\n\n")

print("dummy_barang.sql berhasil dibuat")
