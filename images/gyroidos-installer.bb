inherit image

DEPENDS += "coreutils-native virtual/kernel"

IMAGE_FSTYPES="wic wic.bmap"

INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "gyroidos-installer-initramfs"

GYROIDOS_DATAPART_LABEL = "gyroidosinstall"

do_rootfs[depends] += "virtual/kernel:do_shared_workdir gyroidos-cml-modules:do_image_complete gyroidos-cml-firmware:do_image_complete"

do_rootfs () {
	cml_deploydir="${TOPDIR}/tmp/deploy/images/${MACHINE}"
	cmldata="${cml_deploydir}/gyroidos_image/gyroidos_datapartition"

	machine_replaced=$(echo "${MACHINE}" | tr "_" "-")

	bbnote "Starting to create gyroidos image"

	rootfs_datadir="${IMAGE_ROOTFS}/userdata/"

	rm -fr "${rootfs_datadir}"
	install -d "${rootfs_datadir}"

	# copy files created by gyroidos-cml recipe to installer data directory
	bbnote "Preparing files for data partition"

	install -d "${rootfs_datadir}/gyroidos_boot/EFI/BOOT/"

	cp -r "$cmldata" "${rootfs_datadir}/gyroidos_data"
	cp -r --dereference "${cml_deploydir}/cml-kernel/bzImage-initramfs-${machine_replaced}.bin.signed" "${rootfs_datadir}/gyroidos_boot/EFI/BOOT/BOOTX64.EFI"
	cp "${TOPDIR}/../gyroidos/build/yocto/install_gyroidos.sh" "${rootfs_datadir}/"

	# modules.img
	if [ ! -f "${DEPLOY_DIR_IMAGE}/gyroidos-cml-modules-${MACHINE}.squashfs" ]; then
		bbfatal "${DEPLOY_DIR_IMAGE}/gyroidos-cml-modules-${MACHINE}.squashfs does not exist. Image will not be bootable."
	fi
	cp --dereference "${DEPLOY_DIR_IMAGE}/gyroidos-cml-modules-${MACHINE}.squashfs" "${rootfs_datadir}/modules.img"

	# firmware.img
	if [ ! -f "${DEPLOY_DIR_IMAGE}/gyroidos-cml-firmware-${MACHINE}.squashfs" ]; then
		bbfatal "${DEPLOY_DIR_IMAGE}/gyroidos-cml-firmware-${MACHINE}.squashfs does not exist. Image will not be bootable."
	fi
	cp --dereference "${DEPLOY_DIR_IMAGE}/gyroidos-cml-firmware-${MACHINE}.squashfs" "${rootfs_datadir}/firmware.img"

}

# multiconfig dependencies:
# https://elinux.org/images/a/a5/002-1500-SLIDES-multiconfig_inception.pdf
do_rootfs[mcdepends] = " \
    mc:installer::virtual/kernel:do_deploy \
"

ROOTFS_PREPROCESS_COMMAND = ""

IMAGE_POSTPROCESS_COMMAND:append = " deploy_installer_symlink; "

deploy_installer_symlink () {
	mkdir -p "${IMGDEPLOYDIR}/gyroidos_image"
	ln -sf "../${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.wic" "${IMGDEPLOYDIR}/gyroidos_image/gyroidosinstaller.img"
	ln -sf "../${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.wic.bmap" "${IMGDEPLOYDIR}/gyroidos_image/gyroidosinstaller.img.bmap"
}
