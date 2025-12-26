<?php
class BarangController extends Controller {
    private $service;

    public function __construct() {
        $db = (new Database())->getConnection();
        $barangModel = new Barang($db);
        $this->service = new BarangService($barangModel);
    }

    public function index() {
        try {
            $result = $this->service->getAll();
            $this->success($result, 'Data barang berhasil diambil');
        } catch (Exception $e) {
            $this->error('Gagal mengambil data: ' . $e->getMessage(), 500);
        }
    }

    public function show($id) {
        try {
            $result = $this->service->getById((int)$id);
            if ($result) {
                $this->success($result, 'Data barang ditemukan');
            } else {
                $this->error('Data barang tidak ditemukan', 404);
            }
        } catch (Exception $e) {
            $this->error('Gagal mengambil data: ' . $e->getMessage(), 500);
        }
    }

    public function create() {
        $input = $this->getJsonInput();
        if (!$input) {
            $this->error('Data JSON tidak valid', 400);
        }
        try {
            $created = $this->service->create($input);
            $this->success($created, 'Barang berhasil ditambahkan', 201);
        } catch (Exception $e) {
            $this->error('Error: ' . $e->getMessage(), 500);
        }
    }

    public function update($id) {
        if (!$id || !is_numeric($id)) {
            $this->error('ID tidak valid', 400);
        }
        $input = $this->getJsonInput();
        if (!$input) {
            $this->error('Data JSON tidak valid', 400);
        }
        try {
            $this->service->update((int)$id, $input);
            $this->success(null, 'Data barang berhasil diperbarui');
        } catch (Exception $e) {
            $this->error('Error: ' . $e->getMessage(), 500);
        }
    }

    public function delete($id) {
        if (!$id || !is_numeric($id)) {
            $this->error('ID tidak valid', 400);
        }
        try {
            $this->service->delete((int)$id);
            $this->success(null, 'Barang berhasil dihapus');
        } catch (Exception $e) {
            $this->error('Error: ' . $e->getMessage(), 500);
        }
    }
}