package com.yele.blesdklibrary.util;

public class CarCodeUtils {

    /**
     * 筛选车架号
     * @param code 在 0K210827000001-0K210827001200之间的
     * @return
     */
    public static boolean switchCarCode(String code){
        if(code.length() != 14 && !code.startsWith("0K21082700")){
            return false;
        }
        int endCode = Integer.parseInt(code.substring(code.length()-4,code.length()));
        return endCode <= 1200 && endCode >= 1;
    }

    /**
     * 判断车辆是否拥有权限
     * @param devType 设备类型
     * @return
     */
    public static boolean switchCarType(String devType){
        if(devType.equals("S520T")){
            return true;
        } else if(devType.equals("S521T")){
            return true;
        } else if(devType.equals("S053T")){
            return true;
        } else if(devType.startsWith("ES520")){
            return true;
        } else if(devType.startsWith("ES50C")){
            return true;
        }
        return false;
    }



    private String[] carCode = new String[]{"0K210827000001","0K210827000002","0K210827000003", "0K210827000004","0K210827000005","0K210827000006","0K210827000007","0K210827000008", "0K210827000009", "0K210827000010",
            "0K210827000011", "0K210827000012", "0K210827000013", "0K210827000014", "0K210827000015", "0K210827000016", "0K210827000017", "0K210827000018", "0K210827000019", "0K210827000020",
            "0K210827000021", "0K210827000022", "0K210827000023", "0K210827000024", "0K210827000025", "0K210827000026", "0K210827000027", "0K210827000028", "0K210827000029", "0K210827000030",
            "0K210827000031", "0K210827000032", "0K210827000033", "0K210827000034", "0K210827000035", "0K210827000036", "0K210827000037", "0K210827000038", "0K210827000039", "0K210827000040",
            "0K210827000041", "0K210827000042", "0K210827000043", "0K210827000044", "0K210827000045", "0K210827000046", "0K210827000047", "0K210827000048", "0K210827000049", "0K210827000050",
            "0K210827000051", "0K210827000052", "0K210827000053", "0K210827000054", "0K210827000055", "0K210827000056", "0K210827000057", "0K210827000058", "0K210827000059", "0K210827000060",
            "0K210827000061", "0K210827000062", "0K210827000063", "0K210827000064", "0K210827000065", "0K210827000066", "0K210827000067", "0K210827000068", "0K210827000069", "0K210827000070",
            "0K210827000071", "0K210827000072", "0K210827000073", "0K210827000074", "0K210827000075", "0K210827000076", "0K210827000077", "0K210827000078", "0K210827000079", "0K210827000080",
            "0K210827000081", "0K210827000082", "0K210827000083", "0K210827000084", "0K210827000085", "0K210827000086", "0K210827000087", "0K210827000088", "0K210827000089", "0K210827000090",
            "0K210827000091", "0K210827000092", "0K210827000093", "0K210827000094", "0K210827000095", "0K210827000096", "0K210827000097", "0K210827000098", "0K210827000099", "0K210827000100",
            "0K210827000101", "0K210827000102", "0K210827000103", "0K210827000104", "0K210827000105", "0K210827000106", "0K210827000107", "0K210827000108", "0K210827000109", "0K210827000110",
            "0K210827000111", "0K210827000112", "0K210827000113", "0K210827000114", "0K210827000115", "0K210827000116", "0K210827000117", "0K210827000118", "0K210827000119", "0K210827000120",
            "0K210827000121", "0K210827000122", "0K210827000123", "0K210827000124", "0K210827000125", "0K210827000126", "0K210827000127", "0K210827000128", "0K210827000129", "0K210827000130",
            "0K210827000131", "0K210827000132", "0K210827000133", "0K210827000134", "0K210827000135", "0K210827000136", "0K210827000137", "0K210827000138", "0K210827000139", "0K210827000140",
            "0K210827000141", "0K210827000142", "0K210827000143", "0K210827000144", "0K210827000145", "0K210827000146", "0K210827000147", "0K210827000148", "0K210827000149", "0K210827000150",
            "0K210827000151", "0K210827000152", "0K210827000153", "0K210827000154", "0K210827000155", "0K210827000156", "0K210827000157", "0K210827000158", "0K210827000159", "0K210827000160",
            "0K210827000161", "0K210827000162", "0K210827000163", "0K210827000164", "0K210827000165", "0K210827000166", "0K210827000167", "0K210827000168", "0K210827000169", "0K210827000170",
            "0K210827000171", "0K210827000172", "0K210827000173", "0K210827000174", "0K210827000175", "0K210827000176", "0K210827000177", "0K210827000178", "0K210827000179", "0K210827000180",
            "0K210827000181", "0K210827000182", "0K210827000183", "0K210827000184", "0K210827000185", "0K210827000186", "0K210827000187", "0K210827000188", "0K210827000189", "0K210827000190",
            "0K210827000191", "0K210827000192", "0K210827000193", "0K210827000194", "0K210827000195", "0K210827000196", "0K210827000197", "0K210827000198", "0K210827000199", "0K210827000200",
            "0K210827000201", "0K210827000202", "0K210827000203", "0K210827000204", "0K210827000205", "0K210827000206", "0K210827000207", "0K210827000208", "0K210827000209", "0K210827000210",
            "0K210827000211", "0K210827000212", "0K210827000213", "0K210827000214", "0K210827000215", "0K210827000216", "0K210827000217", "0K210827000218", "0K210827000219", "0K210827000220",
            "0K210827000221", "0K210827000222", "0K210827000223", "0K210827000224", "0K210827000225", "0K210827000226", "0K210827000227", "0K210827000228", "0K210827000229", "0K210827000230",
            "0K210827000231", "0K210827000232", "0K210827000233", "0K210827000234", "0K210827000235", "0K210827000236", "0K210827000237", "0K210827000238", "0K210827000239", "0K210827000240",
            "0K210827000241", "0K210827000242", "0K210827000243", "0K210827000244", "0K210827000245", "0K210827000246", "0K210827000247", "0K210827000248", "0K210827000249", "0K210827000250",
            "0K210827000251", "0K210827000252", "0K210827000253", "0K210827000254", "0K210827000255", "0K210827000256", "0K210827000257", "0K210827000258", "0K210827000259", "0K210827000260",
            "0K210827000261", "0K210827000262", "0K210827000263", "0K210827000264", "0K210827000265", "0K210827000266", "0K210827000267", "0K210827000268", "0K210827000269", "0K210827000270",
            "0K210827000271", "0K210827000272", "0K210827000273", "0K210827000274", "0K210827000275", "0K210827000276", "0K210827000277", "0K210827000278", "0K210827000279", "0K210827000280",
            "0K210827000281", "0K210827000282", "0K210827000283", "0K210827000284", "0K210827000285", "0K210827000286", "0K210827000287", "0K210827000288", "0K210827000289", "0K210827000290",
            "0K210827000291", "0K210827000292", "0K210827000293", "0K210827000294", "0K210827000295", "0K210827000296", "0K210827000297", "0K210827000298", "0K210827000299", "0K210827000300",
            "0K210827000301", "0K210827000302", "0K210827000303", "0K210827000304", "0K210827000305", "0K210827000306", "0K210827000307", "0K210827000308", "0K210827000309", "0K210827000310",
            "0K210827000311", "0K210827000312", "0K210827000313", "0K210827000314", "0K210827000315", "0K210827000316", "0K210827000317", "0K210827000318", "0K210827000319", "0K210827000320",
            "0K210827000321", "0K210827000322", "0K210827000323", "0K210827000324", "0K210827000325", "0K210827000326", "0K210827000327", "0K210827000328", "0K210827000329", "0K210827000330",
            "0K210827000331", "0K210827000332", "0K210827000333", "0K210827000334", "0K210827000335", "0K210827000336", "0K210827000337", "0K210827000338", "0K210827000339", "0K210827000340",
            "0K210827000341", "0K210827000342", "0K210827000343", "0K210827000344", "0K210827000345", "0K210827000346", "0K210827000347", "0K210827000348", "0K210827000349", "0K210827000350",
            "0K210827000351", "0K210827000352", "0K210827000353", "0K210827000354", "0K210827000355", "0K210827000356", "0K210827000357", "0K210827000358", "0K210827000359", "0K210827000360",
            "0K210827000361", "0K210827000362", "0K210827000363", "0K210827000364", "0K210827000365", "0K210827000366", "0K210827000367", "0K210827000368", "0K210827000369", "0K210827000370",
            "0K210827000371", "0K210827000372", "0K210827000373", "0K210827000374", "0K210827000375", "0K210827000376", "0K210827000377", "0K210827000378", "0K210827000379", "0K210827000380",
            "0K210827000381", "0K210827000382", "0K210827000383", "0K210827000384", "0K210827000385", "0K210827000386", "0K210827000387", "0K210827000388", "0K210827000389", "0K210827000390",
            "0K210827000391", "0K210827000392", "0K210827000393", "0K210827000394", "0K210827000395", "0K210827000396", "0K210827000397", "0K210827000398", "0K210827000399", "0K210827000400",
            "0K210827000401", "0K210827000402", "0K210827000403", "0K210827000404", "0K210827000405", "0K210827000406", "0K210827000407", "0K210827000408", "0K210827000409", "0K210827000410",
            "0K210827000411", "0K210827000412", "0K210827000413", "0K210827000414", "0K210827000415", "0K210827000416", "0K210827000417", "0K210827000418", "0K210827000419", "0K210827000420",
            "0K210827000421", "0K210827000422", "0K210827000423", "0K210827000424", "0K210827000425", "0K210827000426", "0K210827000427", "0K210827000428", "0K210827000429", "0K210827000430",
            "0K210827000431", "0K210827000432", "0K210827000433", "0K210827000434", "0K210827000435", "0K210827000436", "0K210827000437", "0K210827000438", "0K210827000439", "0K210827000440",
            "0K210827000441", "0K210827000442", "0K210827000443", "0K210827000444", "0K210827000445", "0K210827000446", "0K210827000447", "0K210827000448", "0K210827000449", "0K210827000450",
            "0K210827000451", "0K210827000452", "0K210827000453", "0K210827000454", "0K210827000455", "0K210827000456", "0K210827000457", "0K210827000458", "0K210827000459", "0K210827000460",
            "0K210827000461", "0K210827000462", "0K210827000463", "0K210827000464", "0K210827000465", "0K210827000466", "0K210827000467", "0K210827000468", "0K210827000469", "0K210827000470",
            "0K210827000471", "0K210827000472", "0K210827000473", "0K210827000474", "0K210827000475", "0K210827000476", "0K210827000477", "0K210827000478", "0K210827000479", "0K210827000480",
            "0K210827000481", "0K210827000482", "0K210827000483", "0K210827000484", "0K210827000485", "0K210827000486", "0K210827000487", "0K210827000488", "0K210827000489", "0K210827000490",
            "0K210827000491", "0K210827000492", "0K210827000493", "0K210827000494", "0K210827000495", "0K210827000496", "0K210827000497", "0K210827000498", "0K210827000499", "0K210827000500",
            "0K210827000501", "0K210827000502", "0K210827000503", "0K210827000504", "0K210827000505", "0K210827000506", "0K210827000507", "0K210827000508", "0K210827000509", "0K210827000510",
            "0K210827000511", "0K210827000512", "0K210827000513", "0K210827000514", "0K210827000515", "0K210827000516", "0K210827000517", "0K210827000518", "0K210827000519", "0K210827000520",
            "0K210827000521", "0K210827000522", "0K210827000523", "0K210827000524", "0K210827000525", "0K210827000526", "0K210827000527", "0K210827000528", "0K210827000529", "0K210827000530",
            "0K210827000531", "0K210827000532", "0K210827000533", "0K210827000534", "0K210827000535", "0K210827000536", "0K210827000537", "0K210827000538", "0K210827000539", "0K210827000540",
            "0K210827000541", "0K210827000542", "0K210827000543", "0K210827000544", "0K210827000545", "0K210827000546", "0K210827000547", "0K210827000548", "0K210827000549", "0K210827000550",
            "0K210827000551", "0K210827000552", "0K210827000553", "0K210827000554", "0K210827000555", "0K210827000556", "0K210827000557", "0K210827000558", "0K210827000559", "0K210827000560",
            "0K210827000561", "0K210827000562", "0K210827000563", "0K210827000564", "0K210827000565", "0K210827000566", "0K210827000567", "0K210827000568", "0K210827000569", "0K210827000570",
            "0K210827000571", "0K210827000572", "0K210827000573", "0K210827000574", "0K210827000575", "0K210827000576", "0K210827000577", "0K210827000578", "0K210827000579", "0K210827000580",
            "0K210827000581", "0K210827000582", "0K210827000583", "0K210827000584", "0K210827000585", "0K210827000586", "0K210827000587", "0K210827000588", "0K210827000589", "0K210827000590",
            "0K210827000591", "0K210827000592", "0K210827000593", "0K210827000594", "0K210827000595", "0K210827000596", "0K210827000597", "0K210827000598", "0K210827000599", "0K210827000600",
            "0K210827000601", "0K210827000602", "0K210827000603", "0K210827000604", "0K210827000605", "0K210827000606", "0K210827000607", "0K210827000608", "0K210827000609", "0K210827000610",
            "0K210827000611", "0K210827000612", "0K210827000613", "0K210827000614", "0K210827000615", "0K210827000616", "0K210827000617", "0K210827000618", "0K210827000619", "0K210827000620",
            "0K210827000621", "0K210827000622", "0K210827000623", "0K210827000624", "0K210827000625", "0K210827000626", "0K210827000627", "0K210827000628", "0K210827000629", "0K210827000630",
            "0K210827000631", "0K210827000632", "0K210827000633", "0K210827000634", "0K210827000635", "0K210827000636", "0K210827000637", "0K210827000638", "0K210827000639", "0K210827000640",
            "0K210827000641", "0K210827000642", "0K210827000643", "0K210827000644", "0K210827000645", "0K210827000646", "0K210827000647", "0K210827000648", "0K210827000649", "0K210827000650",
            "0K210827000651", "0K210827000652", "0K210827000653", "0K210827000654", "0K210827000655", "0K210827000656", "0K210827000657", "0K210827000658", "0K210827000659", "0K210827000660",
            "0K210827000661", "0K210827000662", "0K210827000663", "0K210827000664", "0K210827000665", "0K210827000666", "0K210827000667", "0K210827000668", "0K210827000669", "0K210827000670",
            "0K210827000671", "0K210827000672", "0K210827000673", "0K210827000674", "0K210827000675", "0K210827000676", "0K210827000677", "0K210827000678", "0K210827000679", "0K210827000680",
            "0K210827000681", "0K210827000682", "0K210827000683", "0K210827000684", "0K210827000685", "0K210827000686", "0K210827000687", "0K210827000688", "0K210827000689", "0K210827000690",
            "0K210827000691", "0K210827000692", "0K210827000693", "0K210827000694", "0K210827000695", "0K210827000696", "0K210827000697", "0K210827000698", "0K210827000699", "0K210827000700",
            "0K210827000701", "0K210827000702", "0K210827000703", "0K210827000704", "0K210827000705", "0K210827000706", "0K210827000707", "0K210827000708", "0K210827000709", "0K210827000710",
            "0K210827000711", "0K210827000712", "0K210827000713", "0K210827000714", "0K210827000715", "0K210827000716", "0K210827000717", "0K210827000718", "0K210827000719", "0K210827000720",
            "0K210827000721", "0K210827000722", "0K210827000723", "0K210827000724", "0K210827000725", "0K210827000726", "0K210827000727", "0K210827000728", "0K210827000729", "0K210827000730",
            "0K210827000731", "0K210827000732", "0K210827000733", "0K210827000734", "0K210827000735", "0K210827000736", "0K210827000737", "0K210827000738", "0K210827000739", "0K210827000740",
            "0K210827000741", "0K210827000742", "0K210827000743", "0K210827000744", "0K210827000745", "0K210827000746", "0K210827000747", "0K210827000748", "0K210827000749", "0K210827000750",
            "0K210827000751", "0K210827000752", "0K210827000753", "0K210827000754", "0K210827000755", "0K210827000756", "0K210827000757", "0K210827000758", "0K210827000759", "0K210827000760",
            "0K210827000761", "0K210827000762", "0K210827000763", "0K210827000764", "0K210827000765", "0K210827000766", "0K210827000767", "0K210827000768", "0K210827000769", "0K210827000770",
            "0K210827000771", "0K210827000772", "0K210827000773", "0K210827000774", "0K210827000775", "0K210827000776", "0K210827000777", "0K210827000778", "0K210827000779", "0K210827000780",
            "0K210827000781", "0K210827000782", "0K210827000783", "0K210827000784", "0K210827000785", "0K210827000786", "0K210827000787", "0K210827000788", "0K210827000789", "0K210827000790",
            "0K210827000791", "0K210827000792", "0K210827000793", "0K210827000794", "0K210827000795", "0K210827000796", "0K210827000797", "0K210827000798", "0K210827000799", "0K210827000800",
            "0K210827000801", "0K210827000802", "0K210827000803", "0K210827000804", "0K210827000805", "0K210827000806", "0K210827000807", "0K210827000808", "0K210827000809", "0K210827000810",
            "0K210827000811", "0K210827000812", "0K210827000813", "0K210827000814", "0K210827000815", "0K210827000816", "0K210827000817", "0K210827000818", "0K210827000819", "0K210827000820",
            "0K210827000821", "0K210827000822", "0K210827000823", "0K210827000824", "0K210827000825", "0K210827000826", "0K210827000827", "0K210827000828", "0K210827000829", "0K210827000830",
            "0K210827000831", "0K210827000832", "0K210827000833", "0K210827000834", "0K210827000835", "0K210827000836", "0K210827000837", "0K210827000838", "0K210827000839", "0K210827000840",
            "0K210827000841", "0K210827000842", "0K210827000843", "0K210827000844", "0K210827000845", "0K210827000846", "0K210827000847", "0K210827000848", "0K210827000849", "0K210827000850",
            "0K210827000851", "0K210827000852", "0K210827000853", "0K210827000854", "0K210827000855", "0K210827000856", "0K210827000857", "0K210827000858", "0K210827000859", "0K210827000860",
            "0K210827000861", "0K210827000862", "0K210827000863", "0K210827000864", "0K210827000865", "0K210827000866", "0K210827000867", "0K210827000868", "0K210827000869", "0K210827000870",
            "0K210827000871", "0K210827000872", "0K210827000873", "0K210827000874", "0K210827000875", "0K210827000876", "0K210827000877", "0K210827000878", "0K210827000879", "0K210827000880",
            "0K210827000881", "0K210827000882", "0K210827000883", "0K210827000884", "0K210827000885", "0K210827000886", "0K210827000887", "0K210827000888", "0K210827000889", "0K210827000890",
            "0K210827000891", "0K210827000892", "0K210827000893", "0K210827000894", "0K210827000895", "0K210827000896", "0K210827000897", "0K210827000898", "0K210827000899", "0K210827000900",
            "0K210827000901", "0K210827000902", "0K210827000903", "0K210827000904", "0K210827000905", "0K210827000906", "0K210827000907", "0K210827000908", "0K210827000909", "0K210827000910",
            "0K210827000911", "0K210827000912", "0K210827000913", "0K210827000914", "0K210827000915", "0K210827000916", "0K210827000917", "0K210827000918", "0K210827000919", "0K210827000920",
            "0K210827000921", "0K210827000922", "0K210827000923", "0K210827000924", "0K210827000925", "0K210827000926", "0K210827000927", "0K210827000928", "0K210827000929", "0K210827000930",
            "0K210827000931", "0K210827000932", "0K210827000933", "0K210827000934", "0K210827000935", "0K210827000936", "0K210827000937", "0K210827000938", "0K210827000939", "0K210827000940",
            "0K210827000941", "0K210827000942", "0K210827000943", "0K210827000944", "0K210827000945", "0K210827000946", "0K210827000947", "0K210827000948", "0K210827000949", "0K210827000950",
            "0K210827000951", "0K210827000952", "0K210827000953", "0K210827000954", "0K210827000955", "0K210827000956", "0K210827000957", "0K210827000958", "0K210827000959", "0K210827000960",
            "0K210827000961", "0K210827000962", "0K210827000963", "0K210827000964", "0K210827000965", "0K210827000966", "0K210827000967", "0K210827000968", "0K210827000969", "0K210827000970",
            "0K210827000971", "0K210827000972", "0K210827000973", "0K210827000974", "0K210827000975", "0K210827000976", "0K210827000977", "0K210827000978", "0K210827000979", "0K210827000980",
            "0K210827000981", "0K210827000982", "0K210827000983", "0K210827000984", "0K210827000985", "0K210827000986", "0K210827000987", "0K210827000988", "0K210827000989", "0K210827000990",
            "0K210827000991", "0K210827000992", "0K210827000993", "0K210827000994", "0K210827000995", "0K210827000996", "0K210827000997", "0K210827000998", "0K210827000999", "0K210827001000",
            "0K210827001001", "0K210827001002", "0K210827001003", "0K210827001004", "0K210827001005", "0K210827001006", "0K210827001007", "0K210827001008", "0K210827001009", "0K210827001010",
            "0K210827001011", "0K210827001012", "0K210827001013", "0K210827001014", "0K210827001015", "0K210827001016", "0K210827001017", "0K210827001018", "0K210827001019", "0K210827001020",
            "0K210827001021", "0K210827001022", "0K210827001023", "0K210827001024", "0K210827001025", "0K210827001026", "0K210827001027", "0K210827001028", "0K210827001029", "0K210827001030",
            "0K210827001031", "0K210827001032", "0K210827001033", "0K210827001034", "0K210827001035", "0K210827001036", "0K210827001037", "0K210827001038", "0K210827001039", "0K210827001040",
            "0K210827001041", "0K210827001042", "0K210827001043", "0K210827001044", "0K210827001045", "0K210827001046", "0K210827001047", "0K210827001048", "0K210827001049", "0K210827001050",
            "0K210827001051", "0K210827001052", "0K210827001053", "0K210827001054", "0K210827001055", "0K210827001056", "0K210827001057", "0K210827001058", "0K210827001059", "0K210827001060",
            "0K210827001061", "0K210827001062", "0K210827001063", "0K210827001064", "0K210827001065", "0K210827001066", "0K210827001067", "0K210827001068", "0K210827001069", "0K210827001070",
            "0K210827001071", "0K210827001072", "0K210827001073", "0K210827001074", "0K210827001075", "0K210827001076", "0K210827001077", "0K210827001078", "0K210827001079", "0K210827001080",
            "0K210827001081", "0K210827001082", "0K210827001083", "0K210827001084", "0K210827001085", "0K210827001086", "0K210827001087", "0K210827001088", "0K210827001089", "0K210827001090",
            "0K210827001091", "0K210827001092", "0K210827001093", "0K210827001094", "0K210827001095", "0K210827001096", "0K210827001097", "0K210827001098", "0K210827001099", "0K210827001100",
            "0K210827001101", "0K210827001102", "0K210827001103", "0K210827001104", "0K210827001105", "0K210827001106", "0K210827001107", "0K210827001108", "0K210827001109", "0K210827001110",
            "0K210827001111", "0K210827001112", "0K210827001113", "0K210827001114", "0K210827001115", "0K210827001116", "0K210827001117", "0K210827001118", "0K210827001119", "0K210827001120",
            "0K210827001121", "0K210827001122", "0K210827001123", "0K210827001124", "0K210827001125", "0K210827001126", "0K210827001127", "0K210827001128", "0K210827001129", "0K210827001130",
            "0K210827001131", "0K210827001132", "0K210827001133", "0K210827001134", "0K210827001135", "0K210827001136", "0K210827001137", "0K210827001138", "0K210827001139", "0K210827001140",
            "0K210827001141", "0K210827001142", "0K210827001143", "0K210827001144", "0K210827001145", "0K210827001146", "0K210827001147", "0K210827001148", "0K210827001149", "0K210827001150",
            "0K210827001151", "0K210827001152", "0K210827001153", "0K210827001154", "0K210827001155", "0K210827001156", "0K210827001157", "0K210827001158", "0K210827001159", "0K210827001160",
            "0K210827001161", "0K210827001162", "0K210827001163", "0K210827001164", "0K210827001165", "0K210827001166", "0K210827001167", "0K210827001168", "0K210827001169", "0K210827001170",
            "0K210827001171", "0K210827001172", "0K210827001173", "0K210827001174", "0K210827001175", "0K210827001176", "0K210827001177", "0K210827001178", "0K210827001179", "0K210827001180",
            "0K210827001181", "0K210827001182", "0K210827001183", "0K210827001184", "0K210827001185", "0K210827001186", "0K210827001187", "0K210827001188", "0K210827001189", "0K210827001190",
            "0K210827001191", "0K210827001192", "0K210827001193", "0K210827001194", "0K210827001195", "0K210827001196", "0K210827001197", "0K210827001198", "0K210827001199", "0K210827001200"};



}
