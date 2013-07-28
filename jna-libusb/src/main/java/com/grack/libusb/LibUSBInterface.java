package com.grack.libusb;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.grack.libusb.jna.libusb_interface;
import com.grack.libusb.jna.libusb_interface_descriptor;

public class LibUSBInterface {
	private libusb_interface iface;
	private LibUSBDevice device;
	private ImmutableList<LibUSBInterfaceDescriptor> altSettings;

	LibUSBInterface(final LibUSBDevice device, libusb_interface iface) {
		this.device = device;
		this.iface = iface;

		if (numAltSettings() == 0) {
			// This may not be possible, but may as well be defensive here
			altSettings = ImmutableList.of();
		} else {
			List<libusb_interface_descriptor> altsettings = Arrays.asList(iface.altsetting.toArray(numAltSettings()));

			altSettings = ImmutableList.copyOf(Lists.transform(altsettings,
					new Function<libusb_interface_descriptor, LibUSBInterfaceDescriptor>() {
						public LibUSBInterfaceDescriptor apply(libusb_interface_descriptor input) {
							return new LibUSBInterfaceDescriptor(device, input);
						}
					}));
		}
	}

	public int numAltSettings() {
		return this.iface.num_altsetting;
	}

	public Iterable<LibUSBInterfaceDescriptor> altSettings() {
		return altSettings;
	}

	@Override
	public String toString() {
		// The interface number is actually available in the altsettings -
		// libusb oddity. If for some reason altSessings is empty, just display
		// (?) instead.
		return "Interface #" + (altSettings.size() == 0 ? "(?)" : altSettings.get(0).descriptor.bInterfaceNumber) + " for " + device;
	}
}