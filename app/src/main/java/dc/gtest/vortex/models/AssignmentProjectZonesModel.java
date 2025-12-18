package dc.gtest.vortex.models;

import java.util.List;

import lombok.Data;

@Data
public class AssignmentProjectZonesModel {
        private int AssignmentId;
        private int ProjectId;
        private List<ZoneModel> ProjectZoneData;
}
