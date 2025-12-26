<?php

class BarangService {
    private $barang; 

    public function __construct(Barang $barang) {
        $this->barang = $barang;
    }

    public function getAll() {
        $stmt = $this->barang->getAll();
        $results = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Convert snake_case to camelCase for Java client
        return array_map(function($row) {
            return [
                'idBarang' => (int)$row['id_barang'],
                'namaBarang' => $row['nama_barang'],
                'kategori' => $row['kategori'],
                'stok' => (int)$row['stok'],
                'tanggalMasuk' => $row['tanggal_masuk']
            ];
        }, $results);
    }

    public function getById(int $id) {
        $this->barang->idBarang = $id;
        $row = $this->barang->getById();
        
        if ($row) {
            return [
                'idBarang' => (int)$row['id_barang'],
                'namaBarang' => $row['nama_barang'],
                'kategori' => $row['kategori'],
                'stok' => (int)$row['stok'],
                'tanggalMasuk' => $row['tanggal_masuk']
            ];
        }
        
        return false;
    }

    public function create(array $input) {
        // Validate required fields
        $this->validateRequired($input, ['namaBarang', 'kategori', 'stok']);
        $input = $this->sanitize($input);
        
        $this->barang->namaBarang = $input['namaBarang'];
        $this->barang->kategori = $input['kategori'];
        $this->barang->stok = (int)$input['stok'];
        $this->barang->tanggalMasuk = isset($input['tanggalMasuk']) && !empty($input['tanggalMasuk']) 
            ? $input['tanggalMasuk'] 
            : date('Y-m-d');
        
        if ($this->barang->create()) {
            $createdData = [
                'idBarang' => (int)$this->barang->idBarang,
                'namaBarang' => $this->barang->namaBarang,
                'kategori' => $this->barang->kategori,
                'stok' => (int)$this->barang->stok,
                'tanggalMasuk' => $this->barang->tanggalMasuk
            ];
            $this->notifyRealTime('barang_updated', ['action' => 'create', 'data' => $createdData]);
            return $createdData;
        }
        throw new Exception('Gagal menambahkan barang');
    }

    public function update(int $id, array $input) {
        // Validate required fields
        $this->validateRequired($input, ['nama_barang', 'kategori', 'stok']);
        $input = $this->sanitize($input);
        
        $this->barang->idBarang = $id;
        $this->barang->namaBarang = $input['nama_barang'];
        $this->barang->kategori = $input['kategori'];
        $this->barang->stok = (int)$input['stok'];
        $this->barang->tanggalMasuk = isset($input['tanggal_masuk']) && !empty($input['tanggal_masuk']) 
            ? $input['tanggal_masuk'] 
            : date('Y-m-d');
        
        if (!$this->barang->update()) {
            throw new Exception('Gagal memperbarui data atau data tidak ditemukan');
        }
        $this->notifyRealTime('barang_updated', ['action' => 'update', 'id' => $id]);
    }

    public function delete(int $id) {
        $this->barang->idBarang = $id;
        if (!$this->barang->delete()) {
            throw new Exception('Gagal menghapus data atau data tidak ditemukan');
        }
        $this->notifyRealTime('barang_updated', ['action' => 'delete', 'id' => $id]);
    }

    private function validateRequired(array $input, array $requiredFields): void {
        $missing = [];
        foreach ($requiredFields as $field) {
            if (!isset($input[$field])) {
                $missing[] = $field;
            } else if (is_string($input[$field]) && empty(trim($input[$field]))) {
                $missing[] = $field;
            }
        }
        if (!empty($missing)) {
            throw new Exception('Field wajib: ' . implode(', ', $missing));
        }
    }

    private function sanitize($data) {
        if (is_array($data)) {
            return array_map([$this, 'sanitize'], $data);
        }
        if (is_numeric($data)) {
            return $data;
        }
        if (is_string($data)) {
            return htmlspecialchars(strip_tags(trim($data)), ENT_QUOTES, 'UTF-8');
        }
        return $data;
    }

    private function notifyRealTime(string $event, array $data): void {
        $payload = json_encode(['event' => $event, 'data' => $data]);

        $options = [
            'http' => [
                'method'  => 'POST',
                'header'  => "Content-Type: application/json\r\n" .
                    "Content-Length: " . strlen($payload) . "\r\n",
                'content' => $payload,
                'timeout' => 3.0,
                'ignore_errors' => true,
            ]
        ];

        $context = stream_context_create($options);
        @file_get_contents('http://localhost:3000/notify', false, $context);
    }
}