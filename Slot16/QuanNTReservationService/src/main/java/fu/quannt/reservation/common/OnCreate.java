package fu.quannt.reservation.common;

import jakarta.validation.groups.Default;

/** Validation group for fields that are mandatory only on create requests. */
public interface OnCreate extends Default {
}
