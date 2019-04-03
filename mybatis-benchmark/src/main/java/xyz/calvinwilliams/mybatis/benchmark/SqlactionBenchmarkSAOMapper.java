package xyz.calvinwilliams.mybatis.benchmark;

import java.util.*;

public interface SqlactionBenchmarkSAOMapper {
    public void insertOne(SqlactionBenchmarkSAO sqlactionBenchmark);
    public void updateOneByName(SqlactionBenchmarkSAO sqlactionBenchmark);
    public SqlactionBenchmarkSAO selectOneByName(String name);
    public List<SqlactionBenchmarkSAO> selectAll();
    public void deleteOneByName(String name);
    public void deleteAll();
}
