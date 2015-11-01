-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2015-11-01 10:16:50
-- 服务器版本: 5.5.44-0ubuntu0.14.04.1
-- PHP 版本: 5.5.9-1ubuntu4.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 数据库: `db_contacts`
--

-- --------------------------------------------------------

--
-- 表的结构 `tbl_calllogs`
--

CREATE TABLE IF NOT EXISTS `tbl_calllogs` (
  `uid` int(11) NOT NULL,
  `fname` char(12) DEFAULT NULL,
  `fphone` char(12) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL,
  `long_date` bigint(20) NOT NULL,
  PRIMARY KEY (`uid`,`fphone`,`long_date`),
  UNIQUE KEY `long_date` (`long_date`),
  KEY `uid` (`uid`),
  KEY `long_date_2` (`long_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `tbl_contacts`
--

CREATE TABLE IF NOT EXISTS `tbl_contacts` (
  `uid` int(11) unsigned NOT NULL,
  `fname` char(12) DEFAULT NULL,
  `fphone` char(24) NOT NULL,
  PRIMARY KEY (`uid`,`fphone`),
  KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `tbl_msgs`
--

CREATE TABLE IF NOT EXISTS `tbl_msgs` (
  `uid` int(11) NOT NULL,
  `from_user` char(24) NOT NULL,
  `msg` varchar(300) DEFAULT NULL,
  `long_date` bigint(20) NOT NULL,
  PRIMARY KEY (`uid`,`from_user`,`long_date`),
  KEY `uid` (`uid`,`long_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `tbl_user_info`
--

CREATE TABLE IF NOT EXISTS `tbl_user_info` (
  `username` char(12) NOT NULL,
  `passwd` char(41) NOT NULL,
  `uid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `username_2` (`username`),
  KEY `username` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- 转存表中的数据 `tbl_user_info`
--

INSERT INTO `tbl_user_info` (`username`, `passwd`, `uid`) VALUES
('tornado', 'e10adc3949ba59abbe56e057f20f883e', 1);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
