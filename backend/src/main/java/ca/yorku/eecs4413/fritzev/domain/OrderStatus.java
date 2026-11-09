package ca.yorku.eecs4413.fritzev.domain;

// Mirrors the CHECK constraint in schema.sql - keep both in sync.
public enum OrderStatus { PENDING, COMPLETED, PAYMENT_FAILED, CANCELLED }
