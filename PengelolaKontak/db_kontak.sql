-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Nov 27, 2024 at 01:09 AM
-- Server version: 10.6.10-MariaDB-log
-- PHP Version: 7.4.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_kontak`
--

-- --------------------------------------------------------

--
-- Table structure for table `pengelola_kontak`
--

CREATE TABLE `pengelola_kontak` (
  `id` int(11) NOT NULL,
  `nama` varchar(55) NOT NULL,
  `telepon` varchar(13) NOT NULL,
  `kategori` enum('Keluarga','Teman','Kerja') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pengelola_kontak`
--

INSERT INTO `pengelola_kontak` (`id`, `nama`, `telepon`, `kategori`) VALUES
(4, 'Bambang', '0828284431233', 'Keluarga'),
(5, 'Agus', '0822213828488', 'Teman'),
(6, 'Riska', '08221822324', 'Kerja');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `pengelola_kontak`
--
ALTER TABLE `pengelola_kontak`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `pengelola_kontak`
--
ALTER TABLE `pengelola_kontak`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
