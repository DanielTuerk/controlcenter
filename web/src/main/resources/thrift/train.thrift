namespace java net.wbz.moba.controlcenter.iface.thrift

include "common.thrift"

struct Train {
  1: required i64 id;
  2: required string name;
  3: required TrainFunction functions;
}

struct TrainFunction {
  1: required i64 id;
  2: required common.BusDataConfiguration busDataConfiguration;
  3: required bool active;
}