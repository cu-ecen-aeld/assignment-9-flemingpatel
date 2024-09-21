# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# The following license files were not able to be identified and are
# represented as "Unknown" below, you will need to check them yourself:
#   LICENSE

SUMMARY = "LDD Kernel Modules and SCULL Driver"
DESCRIPTION = "Builds and installs misc-modules and scull kernel modules along with associated scripts."
SECTION = "kernel/modules"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://git@github.com/cu-ecen-aeld/assignment-7-flemingpatel.git;protocol=ssh;branch=master"
SRC_URI += "file://S98lddmodules"

# Modify these as desired
PV = "1.0+git${SRCPV}"
SRCREV = "f331d6fbcfcf4491a262ad8a2a4551871e11995f"

S = "${WORKDIR}/git"

inherit module update-rc.d

DEPENDS += "virtual/kernel (>= 5.15)"

EXTRA_OEMAKE = "KERNELDIR=${STAGING_KERNEL_DIR} -C ${STAGING_KERNEL_DIR} M=${S}"

INITSCRIPT_NAME = "S98lddmodules"
INITSCRIPT_PARAMS = "defaults 98"

FILES:${PN} += "${sysconfdir}/init.d/${INITSCRIPT_NAME} \
                ${bindir}/module_load \
                ${bindir}/module_unload \
                ${bindir}/scull_load \
                ${bindir}/scull_unload"

do_configure:append() {
    # Modify Makefile
    sed -i '/^SUBDIRS =/,/^$/c\SUBDIRS = misc-modules scull\n' ${S}/Makefile
}

do_compile() {
    oe_runmake -C ${S}
}

do_install() {
    # Install kernel modules
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/etc
    install -m 0755 ${S}/misc-modules/faulty.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/etc/
    install -m 0755 ${S}/misc-modules/hello.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/etc/
    install -m 0755 ${S}/scull/scull.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/etc/

    # Install helper scripts
    install -d ${D}${bindir}
    install -m 0755 ${S}/misc-modules/module_load ${D}${bindir}/
    install -m 0755 ${S}/misc-modules/module_unload ${D}${bindir}/
    install -m 0755 ${S}/scull/scull_load ${D}${bindir}/
    install -m 0755 ${S}/scull/scull_unload ${D}${bindir}/

    # Replace path
    sed -i "s|insmod .\/|insmod /lib/modules/${KERNEL_VERSION}/etc/|g" ${D}${bindir}/scull_load
    sed -i "s|insmod .\/|insmod /lib/modules/${KERNEL_VERSION}/etc/|g" ${D}${bindir}/module_load

    # Install the init script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/S98lddmodules ${D}${sysconfdir}/init.d/
}

#do_install:append() {
#    depmod -a -b ${D} ${KERNEL_VERSION}
#}

RDEPENDS_${PN} += "module-init-tools"
#RPROVIDES_${PN} += "kernel-module-faulty kernel-module-hello kernel-module-scull"

