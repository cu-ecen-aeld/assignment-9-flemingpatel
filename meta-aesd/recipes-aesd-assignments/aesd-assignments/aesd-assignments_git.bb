# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://git@github.com/cu-ecen-aeld/assignments-3-and-later-flemingpatel;protocol=ssh;branch=master"
PV = "1.0+git${SRCPV}"
SRCREV = "64d7a7f7d9d45321cd57a8a13a83a76454b9c270"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "server" directory here to build from the "server" directory
# in your assignments repo
S = "${WORKDIR}/git/server"

# add the aesdsocket application and any other files you need to install
# See https://git.yoctoproject.org/poky/plain/meta/conf/bitbake.conf?h=kirkstone
FILES:${PN} += "${bindir}/aesdsocket"

# customize these as necessary for any libraries you need for your application
TARGET_LDFLAGS += "-pthread"

inherit update-rc.d
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "aesdsocket-start-stop"

do_configure () {
	:
}

do_compile () {
	oe_runmake
}

do_install () {
	install -d ${D}${bindir}
	install -m 0775 ${S}/aesdsocket ${D}${bindir}/

	install -d ${D}${sysconfdir}/init.d
	install -m 0775 ${S}/aesdsocket-start-stop ${D}${sysconfdir}/init.d
	sed -i 's|bash|sh|g' ${D}${sysconfdir}/init.d/aesdsocket-start-stop
}
