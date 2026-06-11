# When CI_CONSOLE_REDIRECT=y, GYROIDOS_LOGTTY is overridden to ttyS2 (see
# conf/gyroidos/genericx86-64.inc) so CML's init log goes to a QEMU serial
# backend instead of the host-invisible tty11 VT. This guard must use the same
# gate as GYROIDOS_LOGTTY, else LOGTTY would point at a node that was not created.
#
# Fragment 15-redirect-logtty does `exec > /dev/$LOGTTY` BEFORE fragment 20
# mounts devtmpfs, so /dev/ttyS2 must already exist as a static node in the
# initramfs rootfs at that point. The kernel's 8250 driver is built-in and
# registered well before userspace, so writes to the static node go straight
# to the UART. Major/minor 4/66 match what devtmpfs exposes for ttyS2 once it
# mounts, so the open FD keeps working after the shadow mount.
do_install:append () {
	if [ "y" = "${CI_CONSOLE_REDIRECT}" ]; then
		mknod -m 622 ${D}/dev/ttyS2 c 4 66
	fi
}
