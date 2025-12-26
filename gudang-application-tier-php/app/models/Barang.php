<?php
class Barang extends Model {

    public $idBarang;
    public $namaBarang;
    public $kategori;
    public $stok;
    public $tanggalMasuk;

    public function __construct($db) {
        parent::__construct($db);
        $this->table = "barang";
    }

    public function getAll() {
        $query = "SELECT * FROM {$this->table} ORDER BY id_barang ASC";
        return $this->executeQuery($query);
    }

    public function getById() {
        $query = "SELECT * FROM {$this->table} WHERE id_barang = :id LIMIT 1";
        $stmt = $this->executeQuery($query, [':id' => $this->idBarang]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($row) {
            $this->namaBarang = $row['nama_barang'];
            $this->kategori = $row['kategori'];
            $this->stok = $row['stok'];
            $this->tanggalMasuk = $row['tanggal_masuk'];
            return $row;
        }

        return false;
    }

    public function create() {
        $query = "INSERT INTO {$this->table} 
                  (nama_barang, kategori, stok, tanggal_masuk) 
                  VALUES (:nama_barang, :kategori, :stok, :tanggal_masuk)";

        $params = [
            ':nama_barang' => $this->namaBarang,
            ':kategori' => $this->kategori,
            ':stok' => $this->stok,
            ':tanggal_masuk' => $this->tanggalMasuk
        ];

        $stmt = $this->executeQuery($query, $params);

        if ($stmt) {
            $this->idBarang = $this->conn->lastInsertId();
            return true;
        }

        return false;
    }

    public function update() {
        $query = "UPDATE {$this->table} 
                  SET nama_barang = :nama_barang, 
                      kategori = :kategori, 
                      stok = :stok,
                      tanggal_masuk = :tanggal_masuk
                  WHERE id_barang = :id";

        $params = [
            ':id' => $this->idBarang,
            ':nama_barang' => $this->namaBarang,
            ':kategori' => $this->kategori,
            ':stok' => $this->stok,
            ':tanggal_masuk' => $this->tanggalMasuk
        ];

        $stmt = $this->executeQuery($query, $params);
        return $stmt->rowCount() > 0;
    }

    public function delete() {
        $query = "DELETE FROM {$this->table} WHERE id_barang = :id";
        $stmt = $this->executeQuery($query, [':id' => $this->idBarang]);
        return $stmt->rowCount() > 0;
    }
}