SUMMARY = "AESD Char driver"
DESCRIPTION = "Builds and installs AESD Char driver along with associated scripts."
SECTION = "kernel/modules"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://git@github.com/cu-ecen-aeld/assignments-3-and-later-flemingpatel;protocol=ssh;branch=master"
SRC_URI += "file://S99aesdcharmodule"

# Modify these as desired
PV = "1.0+git${SRCPV}"
SRCREV = "5213c45876d834cd74e8052a465dd5d419280a99"

S = "${WORKDIR}/git/aesd-char-driver"

inherit module update-rc.d

DEPENDS += "virtual/kernel (>= 5.15)"

EXTRA_OEMAKE = "KERNELDIR=${STAGING_KERNEL_DIR} -C ${STAGING_KERNEL_DIR} M=${S}"

INITSCRIPT_NAME = "S99aesdcharmodule"
INITSCRIPT_PARAMS = "defaults 99"

FILES:${PN} += "${sysconfdir}/init.d/${INITSCRIPT_NAME} \
                ${bindir}/aesdchar_load \
                ${bindir}/aesdchar_unload"

do_compile() {
    oe_runmake -C ${S}
}

do_install() {
    # Install kernel modules
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/etc
    install -m 0755 ${S}/aesdchar.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/etc/

    # Install helper scripts
    install -d ${D}${bindir}
    install -m 0755 ${S}/aesdchar_load ${D}${bindir}/
    install -m 0755 ${S}/aesdchar_unload ${D}${bindir}/

    # Replace path
    sed -i "s|\${module}\.ko|/lib/modules/${KERNEL_VERSION}/etc/\${module}.ko|g" ${D}${bindir}/aesdchar_load
    sed -i "s|insmod .\/|insmod /lib/modules/${KERNEL_VERSION}/etc/|g" ${D}${bindir}/aesdchar_load

    # Install the init script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/S99aesdcharmodule ${D}${sysconfdir}/init.d/
}

#do_install:append() {
#    depmod -a -b ${D} ${KERNEL_VERSION}
#}

RDEPENDS_${PN} += "module-init-tools"
#RPROVIDES_${PN} += "kernel-module-aesdchar"

