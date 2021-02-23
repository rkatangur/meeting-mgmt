package com.fserv.meeting.helper;

import java.util.Collection;
import java.util.List;

import com.fserv.meeting.vo.ResourceVo;

public interface ResourceHelper<V extends ResourceVo, R> {

  public V buildVo(R resource);

  public List<V> buildVos(Iterable<R> resources);

  public R build(V voResource);

  public Collection<R> build(Iterable<V> resources);
}
