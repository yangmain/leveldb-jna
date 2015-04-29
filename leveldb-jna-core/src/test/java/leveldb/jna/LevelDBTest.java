package leveldb.jna;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class LevelDBTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void open_and_close() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options);
            levelDB.close();
        }
    }

    @Test
    public void open_and_close_twice() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options);
            levelDB.close();
        }
    }

    @Test
    public void open_database_twice() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try(LevelDB levelDB1 = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                try(LevelDB levelDB2 = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                    Assert.fail("Expected LevelDBException about already used database");
                } catch (LevelDBException e) {
                    Assert.assertTrue(e.getMessage().contains("already held by process"));
                }
            }
        }
    }

    @Test
    public void get_null_key() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try(LevelDBReadOptions readOptions = new LevelDBReadOptions()) {
                try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                    byte[] result = levelDB.get(null, readOptions);
                    Assert.assertEquals(null, result);
                }
            }
        }
    }

    @Test
    public void get_not_exists_key() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try(LevelDBReadOptions readOptions = new LevelDBReadOptions()) {
                try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                    byte[] result = levelDB.get(new byte[]{}, readOptions);
                    Assert.assertEquals(null, result);
                }
            }
        }
    }

    @Test
    public void put_null_key() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                    levelDB.put(null, new byte[]{}, writeOptions);
                }
            }
        }
    }

    @Test
    public void put_null_key_and_null_value() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                    levelDB.put(null, null, writeOptions);
                }
            }
        }
    }

    @Test
    public void put_and_get() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            byte[] key = new byte[] {42};
            byte[] value = new byte[] {43};

            try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                    levelDB.put(key, value, writeOptions);
                }

                try(LevelDBReadOptions readOptions = new LevelDBReadOptions()) {
                    byte[] result = levelDB.get(key, readOptions);

                    Assert.assertArrayEquals(value, result);
                }
            }
        }
    }

    @Test
    public void delete_null_key() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                    levelDB.delete(null, writeOptions);
                }
            }
        }
    }

    @Test
    public void delete_not_esists_key() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                    levelDB.delete(new byte[]{42}, writeOptions);
                }
            }
        }
    }

    @Test
    public void delete_key() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            byte[] key = new byte[] {42};
            byte[] value = new byte[] {43};


            try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                    levelDB.put(key, value, writeOptions);
                }

                try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                    levelDB.delete(key, writeOptions);
                }

                try(LevelDBReadOptions readOptions = new LevelDBReadOptions()) {
                    byte[] result = levelDB.get(key, readOptions);

                    Assert.assertEquals(null, result);
                }
            }
        }
    }

    @Test
    public void get_unknown_property() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                String result = levelDB.property("unknown-property");

                Assert.assertEquals(null, result);
            }
        }
    }

    @Test
    public void get_known_property() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                String result = levelDB.property("leveldb.stats");

                Assert.assertNotEquals(null, result);
            }
        }
    }

    @Test
    public void repair_not_esists_database() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            LevelDB.repair(testFolder.getRoot().getAbsolutePath(), options);
        }
    }

    @Test
    public void repair_database() throws Exception {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                    levelDB.put(new byte[] {42}, new byte[] {43}, writeOptions);
                }
            }
        }

        try(LevelDBOptions options = new LevelDBOptions()) {
            LevelDB.repair(testFolder.getRoot().getAbsolutePath(), options);
        }
    }

    @Test
    public void destroy_not_esists_database() {
        try(LevelDBOptions options = new LevelDBOptions()) {
            LevelDB.destroy(testFolder.getRoot().getAbsolutePath(), options);
        }
    }

    @Test
    public void destroy_database() throws Exception {
        try(LevelDBOptions options = new LevelDBOptions()) {
            options.setCreateIfMissing(true);

            try (LevelDB levelDB = new LevelDB(testFolder.getRoot().getAbsolutePath(), options)) {
                try(LevelDBWriteOptions writeOptions = new LevelDBWriteOptions()) {
                    levelDB.put(new byte[] {42}, new byte[] {43}, writeOptions);
                }
            }
        }

        try(LevelDBOptions options = new LevelDBOptions()) {
            LevelDB.destroy(testFolder.getRoot().getAbsolutePath(), options);
        }

        Assert.assertFalse(new File(testFolder.getRoot(), "CURRENT").exists());
    }

    @Test
    public void get_version() {
        Assert.assertTrue(LevelDB.majorVersion() >= 1);
        Assert.assertTrue(LevelDB.minorVersion() >= 0);
    }
}