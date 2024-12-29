namespace java net.wbz.moba.controlcenter.iface.thrift

struct BusDataConfiguration {
  1: required i64 id;
  2: required i32 bus;
  3: required i32 address;
  4: required i32 bit;
  5: required bool bitState;
}