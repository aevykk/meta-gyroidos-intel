MACHINE = "genericx86-64"

INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "gyroidos-cml-initramfs"

IMAGE_FSTYPES = "cpio ext4"
GYROIDOS_FSTYPES = "wic wic.bmap"
GYROIDOS_PARTTABLE_TYPE = "gpt"
GYROIDOS_GUESTOS_ARCH = "qemux86-64"

GYROIDOS_HARDWARE = "x86"

# CML's init log device. Deployable builds use the tty11 VT (host-invisible
# under QEMU -nographic). When CI_CONSOLE_REDIRECT=y it is rerouted to ttyS2 so
# the CML log is written directly to a QEMU serial backend that VM-management.sh
# captures as ${PROCESS_NAME}.cml.log. Requires a matching static /dev/ttyS2
# node — see the cml-boot bbappend in this layer (same gate). Keyed on
# CI_CONSOLE_REDIRECT, not DEVELOPMENT_BUILD, so dev images deployed to hardware
# are not forced onto the CI serial backend.
GYROIDOS_LOGTTY = "${@oe.utils.vartrue('CI_CONSOLE_REDIRECT', 'ttyS2', 'tty11', d)}"

# Dev/CI extras for the assembled kernel command line (see
# meta-gyroidos linux-gyroidos.inc:GYROIDOS_KERNEL_CMDLINE). Routes kernel
# printk to ttyS1 so VM-management.sh captures it as ${PROCESS_NAME}.kernel.log.
# - earlycon=uart8250,io,0x2f8 (= COM2 / ttyS1) catches printk before the
#   regular 8250 console attaches, so early panics still land in the artifact.
# - console=ttyS1,115200 attaches the regular console to the same UART.
GYROIDOS_KERNEL_CMDLINE_DEV = "earlycon=uart8250,io,0x2f8 console=ttyS1,115200"

PKI_UEFI_KEYS = "y"

PACKAGE_CLASSES = "package_ipk"
BBMULTICONFIG = "guestos installer"

PREFERRED_PROVIDER_virtual/kernel:genericx86-64 ?= "linux-rolling-stable"
PREFERRED_PROVIDER_virtual/kernel:gyroidos-cml ?= "linux-rolling-stable"

INITRAMFS_MODULES = ""

INITRAMFS_MAXSIZE = "150000"

PACKAGE_INSTALL:append:pn-gyroidos-cml-initramfs = "${INITRAMFS_MODULES}"
PACKAGE_INSTALL:append:pn-gyroidos-installer-initramfs = "${INITRAMFS_MODULES}"

IMAGE_BOOT_FILES:genericx86-64 = "${KERNEL_DEPLOYSUBDIR}/bzImage-initramfs-${MACHINE}.bin.signed;EFI/BOOT/BOOTX64.EFI"
WKS_FILE:genericx86-64 = "gyroidos-cml.genericx86.wks.in"
