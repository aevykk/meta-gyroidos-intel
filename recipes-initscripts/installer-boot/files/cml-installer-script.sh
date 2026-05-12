#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin

echo "----------------- GyroidOS Installer ------------------"

mkdir -p /lib/firmware
mkdir -p /proc
mkdir -p /sys
mkdir -p /run

mount /proc
mount /sys
mount /dev
mount /run

mkdir -p /dev/pts
mount /dev/pts

mkdir -p /dev/shm
mkdir -p /data

udevd --daemon
udevadm trigger --type=subsystems --action=add
sleep 2
udevadm settle
udevadm trigger --type=devices --action=add
sleep 2
udevadm settle

mount -a

#now modules partition is mounted
echo "Waiting for devices ."
for i in {1..4}; do
	echo -n "."
	udevadm trigger --type=subsystems --action=add
	sleep 2
	udevadm settle
	udevadm trigger --type=devices --action=add
	sleep 2
	udevadm settle
done

mount -a

exec /sbin/init
